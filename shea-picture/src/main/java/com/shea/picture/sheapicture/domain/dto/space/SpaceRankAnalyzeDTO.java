package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间使用排行分析DTO
 * @author : Shea.
 * @since : 2026/4/25 13:36
 */
@Data
public class SpaceRankAnalyzeDTO implements Serializable {

    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}
