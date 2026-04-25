package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间用户上传行为分析DTO
 * @author : Shea.
 * @since : 2026/4/25 13:24
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SpaceUserAnalyzeDTO extends SpaceAnalyzeDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 时间维度，可选值：day, week, month, year
     */
    private String timeDimension;
}
