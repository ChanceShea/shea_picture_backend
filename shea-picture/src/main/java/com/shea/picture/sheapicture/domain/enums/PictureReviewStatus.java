package com.shea.picture.sheapicture.domain.enums;

import lombok.Getter;

/**
 * 图片审核状态枚举
 * @author : Shea.
 * @since : 2026/4/19 16:14
 */
@Getter
public enum PictureReviewStatus {

    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);

    private final String name;
    private final int code;

    PictureReviewStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static PictureReviewStatus getNameByCode(int code) {
        for (PictureReviewStatus value : PictureReviewStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
