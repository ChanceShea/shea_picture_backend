package com.shea.picture.sheapicture.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.SpaceRoleEnum;
import com.shea.picture.sheapicture.domain.enums.SpaceTypeEnum;
import com.shea.picture.sheapicture.manager.auth.model.SpaceUserAuthConfig;
import com.shea.picture.sheapicture.manager.auth.model.SpaceUserPermissionConstant;
import com.shea.picture.sheapicture.manager.auth.model.SpaceUserRole;
import com.shea.picture.sheapicture.service.SpaceUserService;
import com.shea.picture.sheapicture.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : Shea.
 * @since : 2026/4/26 08:59
 */
@Component
public class SpaceUserAuthManager {

    public static final SpaceUserAuthConfig spaceUserAuthConfig;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        spaceUserAuthConfig = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    private final UserService userService;
    private final SpaceUserService spaceUserService;

    public SpaceUserAuthManager(UserService userService, SpaceUserService spaceUserService) {
        this.userService = userService;
        this.spaceUserService = spaceUserService;
    }

    /**
     * 根据空间用户角色获取权限列表
     * @param spaceUserRole 空间用户角色
     * @return 权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (spaceUserRole == null) {
            return new ArrayList<>();
        }
        SpaceUserRole role = spaceUserAuthConfig.getRoles().stream()
                .filter(r -> r.getKey().equals(spaceUserRole))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    public List<String> getPermissions(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        List<String> adminPermission = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        if (space == null) {
            // 公共图库，仅管理员可操作
            if (userService.isAdmin(loginUser)) {
                return adminPermission;
            }
            // 其他用户仅有浏览权限
            return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getSpaceTypeEnumByValue(space.getSpaceType());
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人和管理员可操作
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)) {
                    return adminPermission;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询spaceUser并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }
}
