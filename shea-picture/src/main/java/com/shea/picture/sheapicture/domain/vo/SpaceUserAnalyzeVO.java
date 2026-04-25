package com.shea.picture.sheapicture.domain.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 空间用户上传行为分析VO
 * @author : Shea.
 * @since : 2026/4/25 13:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceUserAnalyzeVO implements Serializable {

    /**
     * 时间区间
     */
    private String period;

    /**
     * 上传图片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
