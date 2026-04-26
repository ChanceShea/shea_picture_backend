package com.shea.picture.sheapicture.domain.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间用户添加DTO
 * @author : Shea.
 * @since : 2026/4/25 21:17
 */
@Data
public class SpaceUserAddDTO implements Serializable {

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
