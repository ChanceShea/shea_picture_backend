package com.shea.picture.sheapicture.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加DTO
 * @author : Shea.
 * @since : 2026/4/18 10:53
 */
@Data
public class UserAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;
}
