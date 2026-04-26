package com.shea.picture.sheapicture.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 空间用户权限配置
 * @author : Shea.
 * @since : 2026/4/26 08:54
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SpaceUserPermission> permissions;

    private List<SpaceUserRole> roles;
}
