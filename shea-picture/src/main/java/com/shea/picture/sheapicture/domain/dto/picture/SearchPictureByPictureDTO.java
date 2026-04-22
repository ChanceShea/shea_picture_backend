package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 以图搜图DTO
 * @author : Shea.
 * @since : 2026/4/22 19:36
 */
@Data
public class SearchPictureByPictureDTO implements Serializable {

    private Long pictureId;

    private static final long serialVersionUID = 1L;
}
