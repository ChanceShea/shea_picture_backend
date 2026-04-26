package com.shea.picture.sheapicture.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserAddDTO;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserQueryDTO;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.SpaceRoleEnum;
import com.shea.picture.sheapicture.domain.vo.SpaceUserVO;
import com.shea.picture.sheapicture.domain.vo.SpaceVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.mapper.SpaceUserMapper;
import com.shea.picture.sheapicture.service.SpaceService;
import com.shea.picture.sheapicture.service.SpaceUserService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author xgw
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
 * @createDate 2026-04-25 21:14:20
 */
@Service
@RequiredArgsConstructor
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserService {

    private final UserService userService;
    private final SpaceService spaceService;


    @Override
    public long addSpaceUser(SpaceUserAddDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(dto, spaceUser);
        validSpaceUser(spaceUser, true);
        boolean save = this.save(spaceUser);
        throwIf(!save, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null) {
            if (spaceRoleEnum == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
            }
        }
    }

    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        SpaceUserVO vo = new SpaceUserVO();
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            vo.setUser(userVO);
        }
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
            vo.setSpace(spaceVO);
        }
        return vo;
    }

    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        if(ObjectUtil.isEmpty(spaceUserList)){
            return Collections.emptyList();
        }
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream().collect(Collectors.groupingBy(Space::getId));
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVO(user));
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));
        });
        return spaceUserVOList;
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryDTO dto) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if(dto == null){
            return queryWrapper;
        }
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getId()),"id", dto.getId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getSpaceId()),"spaceId", dto.getSpaceId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getUserId()),"userId", dto.getUserId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getSpaceRole()),"spaceRole", dto.getSpaceRole());
        return queryWrapper;
    }

    @Override
    public boolean removeSpaceById(DeleteRequest deleteRequest, HttpServletRequest request) {
        return false;
    }
}




