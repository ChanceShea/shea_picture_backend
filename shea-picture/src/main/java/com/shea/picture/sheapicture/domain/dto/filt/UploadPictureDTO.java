package com.shea.picture.sheapicture.domain.dto.filt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传图片DTO
 * @author : Shea.
 * @since : 2026/4/18 19:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadPictureDTO {

    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片缩略图地址
     */
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片大小
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private int picWidth;

    /**
     * 图片高度
     */
    private int picHeight;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 图片颜色
     */
    private String picColor;

}
