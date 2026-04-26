package com.shea.picture.sheapicture.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 空间用户角色
 * @author : Shea.
 * @since : 2026/4/26 08:56
 */
@Data
public class SpaceUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色键
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 角色描述
     */
    private String description;
}
