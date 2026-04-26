package com.shea.picture.sheapicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.domain.dto.space.SpaceAddDTO;
import com.shea.picture.sheapicture.domain.dto.space.SpaceQueryDTO;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.SpaceLevelEnum;
import com.shea.picture.sheapicture.domain.enums.SpaceRoleEnum;
import com.shea.picture.sheapicture.domain.enums.SpaceTypeEnum;
import com.shea.picture.sheapicture.domain.vo.SpaceVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.mapper.SpaceMapper;
import com.shea.picture.sheapicture.service.PictureService;
import com.shea.picture.sheapicture.service.SpaceService;
import com.shea.picture.sheapicture.service.SpaceUserService;
import com.shea.picture.sheapicture.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author xgw
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2026-04-21 08:31:16
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceService {

    private final UserService userService;
    private final TransactionTemplate transactionTemplate;
    private final Map<String,Object> lockMap = new ConcurrentHashMap<>();
    private final PictureService pictureService;
    private final SpaceUserService spaceUserService;

    public SpaceServiceImpl(UserService userService, TransactionTemplate transactionTemplate, @Lazy PictureService pictureService, SpaceUserService spaceUserService) {
        this.userService = userService;
        this.transactionTemplate = transactionTemplate;
        this.pictureService = pictureService;
        this.spaceUserService = spaceUserService;
    }


    @Override
    public long addSpace(SpaceAddDTO dto, User loginUser) {
        // 1. 填充参数默认值
        Space space = new Space();
        BeanUtils.copyProperties(dto, space);
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.NORMAL.getValue());
        }
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        this.fillSpaceBySpaceLevel(space);
        // 2. 校验参数
        this.validSpace(space, true);
        // 3. 校验权限，非管理员用户只能创建普通级别空间
        Long id = loginUser.getId();
        space.setUserId(id);
        if (!Objects.equals(SpaceLevelEnum.NORMAL.getValue(), space.getSpaceLevel()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限创建指定级别的空间");
        }
        // 4. 控制一个用户只能有一个私有空间和一个团队空间
//        String lock = String.valueOf(id).intern();
        Object lock = lockMap.computeIfAbsent(String.valueOf(id), k -> new Object());
        synchronized (lock) {
            // 使用编程式事务而不是使用声明式事务
            // 声明式事务加在方法上，可能导致锁已经释放了，事务还没提交，另一个请求就就会直接进入同步块中
            Long execute = transactionTemplate.execute(status -> {
                // 判断用户是否已有空间
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, id)
                        .eq(Space::getSpaceType,space.getSpaceType())
                        .exists();
                // 如果已有，则不能创建
                throwIf(exists, ErrorCode.OPERATION_ERROR, "用户已存在该类别的空间");
                // 如果没有，则可以创建
                boolean res = this.save(space);
                throwIf(!res, ErrorCode.OPERATION_ERROR, "创建空间失败");
                // 创建成功后，如果是团队空间，则将当前用户关联到空间
                if(Objects.equals(SpaceTypeEnum.TEAM.getValue(), space.getSpaceType())) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(id);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    res = spaceUserService.save(spaceUser);
                    throwIf(!res, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                }
                return space.getId();
            });
            return Optional.ofNullable(execute).orElse(-1L);
        }
    }

    @Override
    public void validSpace(Space space, boolean add) {
        throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getSpaceTypeEnumByText(spaceType);
        if (add) {
            throwIf(StrUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间等级不能为空");
            throwIf(spaceType == null, ErrorCode.PARAMS_ERROR,"空间类别不能为空");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称长度不能超过30");
        }
        if (spaceLevel != null) {
            SpaceLevelEnum spaceLevelByValue = SpaceLevelEnum.getSpaceLevelByValue(spaceLevel);
            if (spaceLevelByValue == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间等级不存在");
            }
        }
        if (spaceType != null) {
            if (spaceTypeEnum == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类别不存在");
            }
        }
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> records = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(records)) {
            return spaceVOPage;
        }
        List<SpaceVO> spaceVOList = records.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());
        Set<Long> userIdSet = records.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            List<User> user = userIdUserListMap.get(userId);
            if (user != null && !user.isEmpty()) {
                UserVO userVO = userService.getUserVO(user.get(0));
                spaceVO.setUser(userVO);
            }
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryDTO dto) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (dto == null) {
            return queryWrapper;
        }
        queryWrapper.eq(dto.getId() != null, "id", dto.getId())
                .eq(dto.getUserId() != null, "userId", dto.getUserId())
                .like(StrUtil.isNotBlank(dto.getSpaceName()), "spaceName", dto.getSpaceName())
                .eq(dto.getSpaceLevel() != null, "spaceLevel", dto.getSpaceLevel())
                .eq(dto.getSpaceType() != null, "spaceType", dto.getSpaceType())
                .orderBy(dto.getSortField() != null, dto.getSortOrder().equals("ascend"), dto.getSortField());
        return queryWrapper;
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum level = SpaceLevelEnum.getSpaceLevelByValue(space.getSpaceLevel());
        if (level != null) {
            // 只有管理员没有填写最大值时，才将默认的最大值填入
            // 方便通过扩展包来对空间进行扩展
            long maxSize = level.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = level.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    @Transactional
    public boolean removeSpaceById(DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Space oldSpace = this.getById(deleteRequest.getId());
        throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        // 验证空间是否属于当前用户或者当前用户是否是管理员，否则抛出无权限错误
        checkSpaceAuth(oldSpace, loginUser);
        boolean result = this.removeById(deleteRequest.getId());
        // 删除空间下的所有图片
        boolean result2 = pictureService.removePictureBySpaceId(deleteRequest, request);
        throwIf(!(result && result2), ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public void checkSpaceAuth(Space space, User loginUser) {
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
        }
    }
}




