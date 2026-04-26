package com.shea.picture.sheapicture.domain.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间用户编辑DTO
 * @author : Shea.
 * @since : 2026/4/25 21:19
 */
@Data
public class SpaceUserEditDTO implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
