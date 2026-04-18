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

    private static final long serialVersionUID = 1L;
}
