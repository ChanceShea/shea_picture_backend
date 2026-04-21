package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑空间DTO
 * @author : Shea.
 * @since : 2026/4/21 08:35
 */
@Data
public class SpaceEditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 空间id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
}
