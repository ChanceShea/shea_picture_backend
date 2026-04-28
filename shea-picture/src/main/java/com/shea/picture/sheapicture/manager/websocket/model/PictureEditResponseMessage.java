package com.shea.picture.sheapicture.manager.websocket.model;

import com.shea.picture.sheapicture.domain.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片编辑响应消息
 * @author : Shea.
 * @since : 2026/4/27 16:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureEditResponseMessage {

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 编辑动作
     */
    private String editAction;

    /**
     * 用户信息
     */
    private UserVO user;
}
