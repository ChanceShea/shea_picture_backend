package com.shea.picture.sheapicture.api.aliyunai.model;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Shea.
 * @since : 2026/4/23 21:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutPaintingDTO {
    /**
     * 模型名称
     */
    @Alias("model")
    private String model;

    /**
     * 输入图像的基本信息
     */
    @Alias("input")
    private Input input;

    /**
     * 输出图像的处理参数
     */
    @Alias("parameters")
    private Parameters parameters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Input {

        /**
         * 图像URL地址或者图像base64数据
         */
        @Alias("image_url")
        private String imageUrl;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {

        /**
         * 逆时针旋转角度，默认值0，取值范围[0, 359]
         */
        @Alias("angle")
        private Integer angle;

        /**
         * 图像宽高比，可选值：["", "1:1", "3:4", "4:3", "9:16", "16:9"]
         */
        @Alias("output_ratio")
        private String outputRatio;

        /**
         * 水平方向扩展比例，默认值1.0，取值范围[1.0, 3.0]
         */
        @Alias("x_scale")
        @JsonProperty("xScale")
        private Float xScale;

        /**
         * 垂直方向扩展比例，默认值1.0，取值范围[1.0, 3.0]
         */
        @Alias("y_scale")
        @JsonProperty("yScale")
        private Float yScale;

        /**
         * 图像上方添加像素，默认值0
         */
        @Alias("top_offset")
        private Integer topOffset;

        /**
         * 图像下方添加像素，默认值0
         */
        @Alias("bottom_offset")
        private Integer bottomOffset;

        /**
         * 图像左侧添加像素，默认值0
         */
        @Alias("left_offset")
        private Integer leftOffset;

        /**
         * 图像右侧添加像素，默认值0
         */
        @Alias("right_offset")
        private Integer rightOffset;

        /**
         * 开启图像最佳质量模式，默认值false
         */
        @Alias("best_quality")
        private Boolean bestQuality;

        /**
         * 限制模型生成的图像文件大小，默认值true
         */
        @Alias("limit_image_size")
        private Boolean limitImageSize;

        /**
         * 添加水印，默认值true
         */
        @Alias("add_watermark")
        private Boolean addWatermark;
    }
}
