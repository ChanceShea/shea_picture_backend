package com.shea.picture.sheapicture.domain.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间创建DTO
 * @author : Shea.
 * @since : 2026/4/21 08:33
 */
@Data
public class SpaceAddDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间类型 0:私有 1:公开
     */
    private Integer spaceType;
}
