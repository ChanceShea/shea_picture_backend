package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/20 08:45
 */
@Data
public class PictureUploadBatchDTO implements Serializable {

    /**
     * 搜索文本
     */
    private String searchText;

    /**
     * 上传数量
     */
    private Integer count = 10;

    /**
     * 文件名前缀
     */
    private String namePrefix;

    private static final long serialVersionUID = 1L;
}
