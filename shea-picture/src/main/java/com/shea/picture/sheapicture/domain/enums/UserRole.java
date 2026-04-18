package com.shea.picture.sheapicture.domain.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 * @author : Shea.
 * @since : 2026/4/18 09:18
 */
@Getter
public enum UserRole {

    USER("用户", "user"),
    ADMIN("管理员", "admin");


    private final String text;

    private final String value;

    UserRole(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据值获取用户角色
     * @param value 值
     * @return 用户角色
     */
    public static UserRole getUserByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRole userRole : UserRole.values()) {
            if (userRole.value.equals(value)) {
                return userRole;
            }
        }
        return null;
    }
}
