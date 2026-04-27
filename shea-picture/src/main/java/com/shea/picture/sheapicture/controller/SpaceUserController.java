package com.shea.picture.sheapicture.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserAddDTO;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserEditDTO;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserQueryDTO;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.SpaceUserVO;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.auth.annotation.SaSpaceCheckPermission;
import com.shea.picture.sheapicture.manager.auth.model.SpaceUserPermissionConstant;
import com.shea.picture.sheapicture.service.SpaceUserService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/25 22:14
 */
@RestController
@RequestMapping("/spaceUser")
@RequiredArgsConstructor
public class SpaceUserController {

    private final SpaceUserService spaceUserService;
    private final UserService userService;


    /**
     * 添加空间用户
     * @param dto 添加空间用户请求
     * @return 空间用户ID
     */
    @PostMapping("/add")
    @SaSpaceCheckPermission(value= SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public Result<Long> addSpaceUser(@RequestBody SpaceUserAddDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        long id = spaceUserService.addSpaceUser(dto);
        return Result.success(id);
    }

    /**
     * 删除空间用户
     * @param deleteRequest 删除空间用户请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @SaSpaceCheckPermission(value= SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public Result<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest) {
        throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = spaceUserService.removeById(id);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取空间用户
     * @param dto 获取空间用户请求
      * @return 空间用户信息
     */
    @PostMapping("/get")
    @SaSpaceCheckPermission(value= SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public Result<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        Long spaceId = dto.getSpaceId();
        Long userId = dto.getUserId();
        throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(dto));
        throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        return Result.success(spaceUser);
    }

    /**
     * 获取空间用户列表
     * @param dto 获取空间用户请求
     * @return 空间用户列表
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value= SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public Result<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.list(spaceUserService.getQueryWrapper(dto));
        return Result.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑空间用户
     * @param dto 编辑空间用户请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value= SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public Result<Boolean> editSpaceUser(@RequestBody SpaceUserEditDTO dto) {
        throwIf(dto == null || dto.getId() <= 0, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtil.copyProperties(dto, spaceUser);
        spaceUserService.validSpaceUser(spaceUser,false);
        Long id = dto.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = spaceUserService.updateById(spaceUser);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 查询我加入的团队空间列表
     * @param request 请求
     * @return 空间用户列表
     */
    @PostMapping("/list/my")
    public Result<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryDTO dto = new SpaceUserQueryDTO();
        dto.setUserId(loginUser.getId());
        List<SpaceUser> list = spaceUserService.list(spaceUserService.getQueryWrapper(dto));
        return Result.success(spaceUserService.getSpaceUserVOList(list));
    }
}
