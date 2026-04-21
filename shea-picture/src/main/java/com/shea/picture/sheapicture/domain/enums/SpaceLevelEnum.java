package com.shea.picture.sheapicture.domain.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 空间等级枚举
 * @author : Shea.
 * @since : 2026/4/21 08:43
 */
@Getter
public enum SpaceLevelEnum {

    NORMAL( "普通版",0,100,100L*1024*1024),
    PROFESSIONAL("专业版",1,1000,1000L*1024*1024),
    ULTIMATE("旗舰版",2,10000,10000L*1024*1024);

    private final String text;
    private final Integer value;
    private final long maxCount;
    private final long maxSize;

    SpaceLevelEnum(String text, Integer value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    public static SpaceLevelEnum getSpaceLevelByValue(Integer value) {
        for (SpaceLevelEnum spaceLevel : SpaceLevelEnum.values()) {
            if (Objects.equals(spaceLevel.getValue(), value)) {
                return spaceLevel;
            }
        }
        return null;
    }
}
