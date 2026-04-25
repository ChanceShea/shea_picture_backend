package com.shea.picture.sheapicture.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/25 12:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceCategoryAnalyzeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片分类
     */
    private String category;

    /**
     * 图片数量
     */
    private Long count;

    /**
     * 图片总大小
     */
    private Long totalSize;
}

