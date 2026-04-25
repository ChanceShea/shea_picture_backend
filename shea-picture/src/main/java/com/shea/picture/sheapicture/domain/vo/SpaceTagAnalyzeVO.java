package com.shea.picture.sheapicture.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间标签分析结果VO
 * @author : Shea.
 * @since : 2026/4/25 13:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceTagAnalyzeVO implements Serializable {

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 图片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
