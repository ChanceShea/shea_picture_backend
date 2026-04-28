package com.shea.picture.sheapicture.manager.websocket.model;

import lombok.Getter;

/**
 * 图片编辑动作枚举
 * @author : Shea.
 * @since : 2026/4/27 16:49
 */
@Getter
public enum PictureEditAction {

    ZOOM_IN("放大","ZOOM_IN"),
    ZOOM_OUT("缩小","ZOOM_OUT"),
    ROTATE_LEFT("左旋","ROTATE_LEFT"),
    ROTATE_RIGHT("右旋","ROTATE_RIGHT");

    private final String text;
    private final String value;

    PictureEditAction(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static PictureEditAction getByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditAction action : PictureEditAction.values()) {
            if (action.value.equals(value)) {
                return action;
            }
        }
        return null;
    }
}
