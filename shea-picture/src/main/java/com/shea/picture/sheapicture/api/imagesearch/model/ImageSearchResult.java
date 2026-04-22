package com.shea.picture.sheapicture.api.imagesearch.model;

import lombok.Data;

/**
 * 图片搜索结果
 * @author : Shea.
 * @since : 2026/4/22 16:04
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 原图地址
     */
    private String fromUrl;
}
