package com.shea.picture.sheapicture.domain.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 空间角色枚举
 * @author : Shea.
 * @since : 2026/4/25 21:33
 */
@Getter
public enum SpaceRoleEnum {
    VIEWER("浏览者","viewer"),
    EDITOR("编辑者","editor"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;

    SpaceRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static SpaceRoleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceRoleEnum spaceRoleEnum : SpaceRoleEnum.values()) {
            if (spaceRoleEnum.value.equals(value)) {
                return spaceRoleEnum;
            }
        }
        return null;
    }
}
