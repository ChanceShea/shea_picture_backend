package com.shea.picture.sheapicture.domain.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 空间类型枚举
 * @author : Shea.
 * @since : 2026/4/21 08:43
 */
@Getter
public enum SpaceTypeEnum {

    PRIVATE( "私有空间",0),
    TEAM("团队空间",1);

    private final String text;
    private final Integer value;

    SpaceTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static SpaceTypeEnum getSpaceTypeEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum sp : SpaceTypeEnum.values()) {
            if (sp.getValue().equals(value)) {
                return sp;
            }
        }
        return null;
    }
}
