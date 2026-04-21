package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/21 08:35
 */
@Data
public class SpaceUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 空间id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间最大存储空间
     */
    private Long maxSize;

    /**
     * 空间最大文件数量
     */
    private Long maxCount;
}
