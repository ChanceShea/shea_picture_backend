package com.shea.picture.sheapicture.domain.dto.picture;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : Shea.
 * @since : 2026/4/21 14:25
 */
@Data
@AllArgsConstructor
public class SpaceLevel {


    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
