package com.shea.picture.sheapicture.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间占用情况分析VO
 * @author : Shea.
 * @since : 2026/4/25 12:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUsageAnalyzeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 已使用空间大小
     */
    private Long usedSize;

    /**
     * 总空间大小
     */
    private Long maxSize;

    /**
     * 空间使用比例
     */
    private Double sizeUsageRatio;

    /**
     * 当前图片数量
     */
    private Long usedCount;

    /**
     * 总图片数量
     */
    private Long maxCount;

    /**
     * 空间图片数量占比
     */
    private Double countUsageRatio;
}
