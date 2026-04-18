package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.domain.dto.user.UserLoginDTO;
import com.shea.picture.sheapicture.domain.dto.user.UserQueryDTO;
import com.shea.picture.sheapicture.domain.dto.user.UserRegisterDTO;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.LoginUserVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xgw
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-04-18 09:05:51
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param dto 用户注册参数
     * @return 用户ID
     */
    long userRegister(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto 用户登录参数
     * @return 用户ID
     */
    LoginUserVO userLogin(UserLoginDTO dto, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取加密密码
     * @param password
     * @return
     */
    String getEncryptPassword(String password);

    /**
     * 用户登出
     * @param request
     * @return 登出结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取用户VO
      * @param user 用户
     * @return 用户VO
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户VO列表
     * @param userList 用户列表
     * @return 用户VO列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取查询条件
     * @param dto 查询参数
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryDTO dto);

    boolean isAdmin(User user);
}
