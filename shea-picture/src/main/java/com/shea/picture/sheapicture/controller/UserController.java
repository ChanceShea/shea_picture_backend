package com.shea.picture.sheapicture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shea.picture.sheapicture.annotation.AuthCheck;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.constant.UserConstant;
import com.shea.picture.sheapicture.domain.dto.user.*;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.LoginUserVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/18 09:42
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        return Result.success(userService.userRegister(dto));
    }

    @PostMapping("/login")
    public Result<LoginUserVO> userLogin(@RequestBody UserLoginDTO dto, HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        return Result.success(userService.userLogin(dto, request));
    }

    @GetMapping("/get/login")
    public Result<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return Result.success(userService.getLoginUserVO(loginUser));
    }

    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request) {
        return Result.success(userService.userLogout(request));
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Long> addUser(@RequestBody UserAddDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        // 默认密码12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encrypt = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encrypt);
        boolean save = userService.save(user);
        throwIf(!save, ErrorCode.OPERATION_ERROR);
        return Result.success(user.getId());
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<User> getUserById(long id) {
        throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return Result.success(user);
    }

    @GetMapping("/get/vo")
    public Result<UserVO> getUserVOById(long id) {
        Result<User> resp = getUserById(id);
        User user = resp.getData();
        return Result.success(userService.getUserVO(user));
    }

    @DeleteMapping("/delete")
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest request) {
        if (request == null || request.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(userService.removeById(request.getId()));
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateUser(@RequestBody UserUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        boolean b = userService.updateById(user);
        throwIf(!b, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 根据查询条件分页获取用户列表
     * @param dto 查询条件
     * @return 用户列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryDTO dto) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        long current = dto.getCurrent();
        long pageSize = dto.getPageSize();
        Page<User> page = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(dto));
        Page<UserVO> userVOPage = new Page<>(current, pageSize,page.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(page.getRecords());
        userVOPage.setRecords(userVOList);
        return Result.success(userVOPage);
    }

}
