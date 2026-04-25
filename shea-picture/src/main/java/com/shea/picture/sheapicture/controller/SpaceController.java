package com.shea.picture.sheapicture.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shea.picture.sheapicture.annotation.AuthCheck;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.constant.UserConstant;
import com.shea.picture.sheapicture.domain.dto.picture.SpaceLevel;
import com.shea.picture.sheapicture.domain.dto.space.SpaceAddDTO;
import com.shea.picture.sheapicture.domain.dto.space.SpaceEditDTO;
import com.shea.picture.sheapicture.domain.dto.space.SpaceQueryDTO;
import com.shea.picture.sheapicture.domain.dto.space.SpaceUpdateDTO;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.SpaceLevelEnum;
import com.shea.picture.sheapicture.domain.vo.SpaceVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.service.SpaceService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;


/**
 * @author : Shea.
 * @since : 2026/4/18 20:17
 */
@RestController
@RequestMapping("/space")
@Slf4j
@RequiredArgsConstructor
public class SpaceController {

    private final UserService userService;
    private final SpaceService spaceService;


    @PostMapping("/add")
    public Result<Long> addSpace(@RequestBody SpaceAddDTO dto, HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long newId = spaceService.addSpace(dto, loginUser);
        return Result.success(newId);
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> removeSpaceById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return Result.success(spaceService.removeSpaceById(deleteRequest, request));
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateSpace(@RequestBody SpaceUpdateDTO dto, HttpServletRequest request) {
        if (dto == null || dto.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Long id = dto.getId();
        Space oldSpace = spaceService.getById(id);
        throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        BeanUtil.copyProperties(dto, oldSpace, CopyOptions.create().setIgnoreNullValue(true));
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(oldSpace);
        // 参数校验
        spaceService.validSpace(oldSpace, false);
        boolean result = spaceService.updateById(oldSpace);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 根据id获取空间
     *
     * @param id      空间id
     * @param request 请求
     * @return 空间信息
     */
    @GetMapping("/get/vo")
    public Result<SpaceVO> getSpaceVOById(Long id, HttpServletRequest request) {
        throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(id);
        throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        return Result.success(SpaceVO.objToVo(space));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryDTO dto, HttpServletRequest request) {
        long current = dto.getCurrent();
        long size = dto.getPageSize();
        Page<Space> page = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(dto));
        return Result.success(page);
    }

    @PostMapping("/list/page/vo")
    public Result<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryDTO dto, HttpServletRequest request) {
        long current = dto.getCurrent();
        long size = dto.getPageSize();
        throwIf(size > 20, ErrorCode.OPERATION_ERROR, "用户查询记录不能超过20条");
        Page<Space> page = spaceService.page(new Page<>(current, size), spaceService.getQueryWrapper(dto));
        return Result.success(spaceService.getSpaceVOPage(page, request));
    }

    @PostMapping("/edit")
    public Result<Boolean> editSpace(@RequestBody SpaceEditDTO dto, HttpServletRequest request) {
        if (dto == null || dto.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Space space = new Space();
        BeanUtils.copyProperties(dto, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 设置编辑时间（用户）
        space.setEditTime(new Date());
        // 数据校验
        spaceService.validSpace(space, false);
        User loginUser = userService.getLoginUser(request);
        Long id = dto.getId();
        Space oldSpace = spaceService.getById(id);
        throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        // 仅本人或者管理员可以编辑
        spaceService.checkSpaceAuth(oldSpace, loginUser);
        boolean result = spaceService.updateById(space);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 获取空间等级列表
     *
     * @return 空间等级列表
     */
    @GetMapping("/list/level")
    public Result<List<SpaceLevel>> getSpaceLevel() {
        List<SpaceLevel> list = Arrays.stream(SpaceLevelEnum.values())
                .map(s -> new SpaceLevel(
                        s.getValue(),
                        s.getText(),
                        s.getMaxCount(),
                        s.getMaxSize()
                )).collect(Collectors.toList());
        return Result.success(list);
    }

}
