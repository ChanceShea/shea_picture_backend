package com.shea.picture.sheapicture.manager.websocket.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;

/**
 * 图片编辑事件生产者
 * @author : Shea.
 * @since : 2026/4/28 08:55
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PictureEditEventProducer {

    private final Disruptor<PictureEditEvent> disruptor;

    /**
     * 发布事件
     * @param pictureEditRequestMessage 图片编辑请求消息
     * @param session WebSocket会话
     * @param user 用户
     * @param pictureId 图片ID
     */
    public void publishEvent(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) {
        RingBuffer<PictureEditEvent> ringBuffer = disruptor.getRingBuffer();
        long next = ringBuffer.next();
        PictureEditEvent pictureEditEvent = ringBuffer.get(next);
        pictureEditEvent.setPictureEditRequestMessage(pictureEditRequestMessage);
        pictureEditEvent.setWebSocketSession(session);
        pictureEditEvent.setUser(user);
        pictureEditEvent.setPictureId(pictureId);
        ringBuffer.publish(next);
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        disruptor.shutdown();
    }
}
