package com.shea.picture.sheapicture.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求参数
 * @author : Shea.
 * @since : 2026/4/18 09:22
 */
@Data
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;
}
