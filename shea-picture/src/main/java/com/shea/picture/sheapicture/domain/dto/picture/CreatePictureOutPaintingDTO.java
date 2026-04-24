package com.shea.picture.sheapicture.domain.dto.picture;

import com.shea.picture.sheapicture.api.aliyunai.model.CreateOutPaintingDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建扩图请求DTO
 * @author : Shea.
 * @since : 2026/4/24 08:48
 */
@Data
public class CreatePictureOutPaintingDTO implements Serializable {

    /**
     * 图片Id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingDTO.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
