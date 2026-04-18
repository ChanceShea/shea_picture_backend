package com.shea.picture.sheapicture.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片标签类别视图对象
 * @author : Shea.
 * @since : 2026/4/18 21:36
 */
@Data
public class PictureTagCategoryVO implements Serializable {

    /**
     * 标签列表
     */
    private List<String> tagList;
    /**
     * 类别列表
     */
    private List<String> categoryList;
}
