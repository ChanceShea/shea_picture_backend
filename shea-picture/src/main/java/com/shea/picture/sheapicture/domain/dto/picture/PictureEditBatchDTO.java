package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片批量编辑DTO
 * @author : Shea.
 * @since : 2026/4/23 10:49
 */
@Data
public class PictureEditBatchDTO implements Serializable {

    /**
     * 图片ID列表
     */
    private List<Long> pictureIds;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 图片分类
     */
    private String category;

    /**
     * 图片标签列表
     */
    private List<String> tags;

    /**
     * 图片命名规则
     */
    private String nameRule;

    private static final long serialVersionUID = 1L;
}
