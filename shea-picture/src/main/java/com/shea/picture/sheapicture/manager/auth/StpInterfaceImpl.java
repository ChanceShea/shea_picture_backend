package com.shea.picture.sheapicture.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.SpaceRoleEnum;
import com.shea.picture.sheapicture.domain.enums.SpaceTypeEnum;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.auth.model.SpaceUserPermissionConstant;
import com.shea.picture.sheapicture.service.PictureService;
import com.shea.picture.sheapicture.service.SpaceService;
import com.shea.picture.sheapicture.service.SpaceUserService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.shea.picture.sheapicture.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author : Shea.
 * @since : 2026/4/26 09:39
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SpaceUserAuthManager spaceUserAuthManager;
    private final SpaceUserService spaceUserService;
    private final UserService userService;
    private final PictureService pictureService;
    private final SpaceService spaceService;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**
     * 返回一个账号所拥有的权限集合
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return 权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 判断loginType，只对类型为space的登录进行校验
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return new ArrayList<>();
        }
        // 管理员权限，表示校验通过
        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 获取上下文对象
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        // 如果所有字段都为空，说明查询的是公共图库，直接通过
        if (isAllFieldsNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }
        // 获取userId
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户未登录");
        }
        Long userId = loginUser.getId();
        // 从上下文中获取SpaceUser对象
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // 如果有spaceUseId，说明是团队空间，通过数据库查询spaceUser对象
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            spaceUser = spaceUserService.getById(spaceUserId);
            if (spaceUser == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未找到空间用户信息");
            }
            // 获取当前登录用户对应的 spaceUser
            SpaceUser loginSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (loginSpaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(loginSpaceUser.getSpaceRole());
        }
        // 没有spaceUserId，尝试通过spaceId或pictureId获取Space对象
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // 如果spaceId为空，说明是公共图库或者没有传spaceId
            // 尝试通过pictureId获取spaceId
            Long pictureId = authContext.getPictureId();
            // 如果没有pictureId，则不用校验，直接通过
            if (pictureId == null) {
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                    .one();
            if (picture == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未找到图片信息");
            }
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人或管理员可操作
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"未找到空间信息");
        }
        // 私有空间，仅空间所有者或管理员可操作
        if(Objects.equals(space.getSpaceType(), SpaceTypeEnum.PRIVATE.getValue())) {
            if (space.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            } else {
                return new ArrayList<>();
            }
        } else {
            // 团队空间，查询spaceUser对应的角色和权限
            spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
            if (spaceUser == null) {
                return new ArrayList<>();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }

    /**
     * 判断对象的所有字段是否为空
     * @param object 对象
     * @return 是否所有字段都为空
     */
    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true;
        }
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                .map(filed -> ReflectUtil.getFieldValue(object, filed))
                .allMatch(ObjectUtil::isEmpty);
    }

    /**
     * 返回一个账号所拥有的角色集合
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return 角色集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 从请求中获取上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest = null;
        // 获取请求参数
        if (ContentType.JSON.getValue().equals(contentType)) {
            // body是以流的形式存在，只能读取一次，所以需要先保存一份
            String body = ServletUtil.getBody(request);
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // 根据请求路径区分id字段的含义
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            // 获取到请求路径的业务前缀
            // /api/picture/aaa?a=1 ==> picture/aaa?a=1
            String requestURI = request.getRequestURI();
            String replaceURI = requestURI.replace(contextPath + "/", "");
            // 获取前缀的第一个斜杠前的字符串
            // picture/aaa?a=1 ==> picture
            String moduleName = StrUtil.subBefore(replaceURI, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                default:
            }
        }
        return authRequest;
    }
}
