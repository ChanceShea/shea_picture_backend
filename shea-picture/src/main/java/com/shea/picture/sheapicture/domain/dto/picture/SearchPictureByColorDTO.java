package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据颜色搜索图片DTO
 * @author : Shea.
 * @since : 2026/4/22 21:11
 */
@Data
public class SearchPictureByColorDTO implements Serializable {

    /**
     * 图片颜色
     */
    private String picColor;

    /**
     * 空间id
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
