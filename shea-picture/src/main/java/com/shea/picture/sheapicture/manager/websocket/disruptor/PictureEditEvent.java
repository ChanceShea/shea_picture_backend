package com.shea.picture.sheapicture.manager.websocket.disruptor;

import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditRequestMessage;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 图片编辑事件
 * @author : Shea.
 * @since : 2026/4/28 08:47
 */
@Data
public class PictureEditEvent {

    /**
     * 图片编辑请求消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * WebSocket会话
     */
    private WebSocketSession webSocketSession;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 图片ID
     */
    private Long pictureId;
}
