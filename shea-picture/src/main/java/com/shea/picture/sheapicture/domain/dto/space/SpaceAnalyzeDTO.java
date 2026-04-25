package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用空间分析请求
 * @author : Shea.
 * @since : 2026/4/24 16:31
 */
@Data
public class SpaceAnalyzeDTO implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否查询公共空间
     */
    private boolean queryPublic;

    /**
     * 是否查询所有图片
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}

