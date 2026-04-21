package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/18 19:06
 */
@Data
public class PictureUploadDTO implements Serializable {

    /**
     * 图片 id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 空间id
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
