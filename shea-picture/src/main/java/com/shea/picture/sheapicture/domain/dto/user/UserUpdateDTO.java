package com.shea.picture.sheapicture.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新DTO
 * @author : Shea.
 * @since : 2026/4/18 10:55
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String userName;
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
