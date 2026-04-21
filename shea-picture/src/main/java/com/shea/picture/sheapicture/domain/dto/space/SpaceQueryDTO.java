package com.shea.picture.sheapicture.domain.dto.space;

import com.shea.picture.sheapicture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询空间DTO
 * @author : Shea.
 * @since : 2026/4/21 08:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryDTO extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 空间id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别
     */
    private Integer spaceLevel;
}
