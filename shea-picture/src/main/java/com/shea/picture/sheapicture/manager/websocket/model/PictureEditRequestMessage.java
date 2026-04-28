package com.shea.picture.sheapicture.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片编辑请求消息
 * @author : Shea.
 * @since : 2026/4/27 16:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类别
     */
    private String type;

    /**
     * 编辑动作
     */
    private String editAction;
}
