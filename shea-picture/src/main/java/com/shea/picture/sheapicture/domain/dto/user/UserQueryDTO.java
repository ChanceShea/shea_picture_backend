package com.shea.picture.sheapicture.domain.dto.user;

import com.shea.picture.sheapicture.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询DTO
 * @author : Shea.
 * @since : 2026/4/18 10:57
 */
@Data
public class UserQueryDTO extends PageRequest implements Serializable {

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
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色
     */
    private String userRole;
}
