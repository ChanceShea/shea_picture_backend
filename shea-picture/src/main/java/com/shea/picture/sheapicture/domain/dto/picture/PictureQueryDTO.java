package com.shea.picture.sheapicture.domain.dto.picture;

import com.shea.picture.sheapicture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 用户查询DTO
 * @author : Shea.
 * @since : 2026/4/18 10:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryDTO extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片简介
     */
    private String introduction;

    /**
     * 图片分类
     */
    private String category;

    /**
     * 图片标签
     */
    private List<String> tags;

    /**
     * 图片大小
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 搜索文本
     */
    private String searchText;

    /**
     * 用户ID
     */
    private Long userId;

}
