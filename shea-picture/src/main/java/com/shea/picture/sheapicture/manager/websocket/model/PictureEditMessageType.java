package com.shea.picture.sheapicture.manager.websocket.model;

import lombok.Getter;

/**
 * 图片编辑消息类型枚举
 * @author : Shea.
 * @since : 2026/4/27 16:46
 */
@Getter
public enum PictureEditMessageType {

    INFO("发送通知","INFO"),
    ERROR("发送错误","ERROR"),
    ENTER_EDIT("进入编辑状态","ENTER_EDIT"),
    EXIT_EDIT("退出编辑状态","EXIT_EDIT"),
    EDIT_ACTION("执行编辑操作","EDIT_ACTION");


    private final String text;
    private final String value;

    PictureEditMessageType(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditMessageType getByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditMessageType type : PictureEditMessageType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
