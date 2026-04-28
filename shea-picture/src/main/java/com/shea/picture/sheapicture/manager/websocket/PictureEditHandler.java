package com.shea.picture.sheapicture.manager.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.manager.websocket.disruptor.PictureEditEventProducer;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditAction;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditMessageType;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditRequestMessage;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditResponseMessage;
import com.shea.picture.sheapicture.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片编辑WebSocket处理器
 * @author : Shea.
 * @since : 2026/4/27 19:19
 */
@Component
@Slf4j
public class PictureEditHandler extends TextWebSocketHandler {

    /**
     * 每张图片的编辑状态，key为图片id，value为正在编辑的用户id
     */
    private final Map<Long,Long> pictureEditingUsers = new ConcurrentHashMap<Long,Long>();

    /**
     * 保存所有连接的会话，key为图片id，value为用户会话集合
     */
    private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private PictureEditEventProducer pictureEditEventProducer;


    /**
     * 处理WebSocket连接建立成功之后的操作
     * @param session 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 保存会话到集合中
        User loginUser = (User)session.getAttributes().get("user");
        Long pictureId = (Long)session.getAttributes().get("pictureId");
        pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
        pictureSessions.get(pictureId).add(session);
        // 构造响应，发送加入编辑的消息通知
        PictureEditResponseMessage perm = new PictureEditResponseMessage();
        perm.setType(PictureEditMessageType.INFO.getValue());
        String msg = String.format("用户%s加入了编辑",loginUser.getUserName());
        perm.setMessage(msg);
        perm.setUser(userService.getUserVO(loginUser));
        broadcastPicture(pictureId, perm);

    }

    /**
     * 处理WebSocket接收到文本消息的操作
     * @param session 会话
     * @param message 消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        // 获取消息内容，将json解析为PictureEditMessage
        PictureEditRequestMessage perm = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        User loginUser = (User)session.getAttributes().get("user");
        Long pictureId = (Long)session.getAttributes().get("pictureId");
        // 生产消息到Disruptor队列中，异步处理ws消息
        pictureEditEventProducer.publishEvent(perm, session, loginUser, pictureId);
    }

    /**
     * 处理编辑操作
     * @param perm
     * @param session
     * @param loginUser
     * @param pictureId
     */
    public void handleEditActionMessage(PictureEditRequestMessage perm, WebSocketSession session, User loginUser, Long pictureId) throws IOException {
        Long editingUserId = pictureEditingUsers.get(pictureId);
        String editAction = perm.getEditAction();
        PictureEditAction actionEnum = PictureEditAction.getByValue(editAction);
        if (actionEnum == null) {
            log.error("无效的编辑动作");
            return;
        }
        // 只有当前用户是正在编辑图片的用户，才可以发送编辑操作
        if (editingUserId != null && editingUserId.equals(loginUser.getId())) {
            // 构造响应，发送加入编辑的消息通知
            PictureEditResponseMessage responseMessage = new PictureEditResponseMessage();
            responseMessage.setType(PictureEditMessageType.EDIT_ACTION.getValue());
            String msg = String.format("用户%s执行了%s动作",loginUser.getUserName(), actionEnum.getText());
            responseMessage.setMessage(msg);
            responseMessage.setUser(userService.getUserVO(loginUser));
            responseMessage.setEditAction(editAction);
            // 广播给除了当前用户之外的所有用户，否则可能造成重复编辑
            broadcastPicture(pictureId, responseMessage,session);
        }
    }

    /**
     * 退出编辑状态
     * @param perm
     * @param session
     * @param loginUser
     * @param pictureId
     */
    public void handleExitEditMessage(PictureEditRequestMessage perm, WebSocketSession session, User loginUser, Long pictureId) throws IOException {
        Long editingUserId = pictureEditingUsers.get(pictureId);
        // 只有当前用户是正在编辑图片的用户，才可以退出编辑状态
        if (editingUserId != null && editingUserId.equals(loginUser.getId())) {
            // 移出当前用户的编辑状态
            pictureEditingUsers.remove(pictureId);
        }
        PictureEditResponseMessage responseMessage = new PictureEditResponseMessage();
        responseMessage.setType(PictureEditMessageType.EXIT_EDIT.getValue());
        String msg = String.format("用户%s退出了编辑",loginUser.getUserName());
        responseMessage.setMessage(msg);
        responseMessage.setUser(userService.getUserVO(loginUser));
        // 广播给所有用户
        broadcastPicture(pictureId, responseMessage);
    }

    /**
     * 进入编辑状态
     * @param perm
     * @param session
     * @param loginUser
     * @param pictureId
     */
    public void handleEnterEditMessage(PictureEditRequestMessage perm, WebSocketSession session, User loginUser, Long pictureId) throws IOException {
        // 只有当没有用户编辑图片时，才可以进入编辑操作
        if (!pictureEditingUsers.containsKey(pictureId)) {
            pictureEditingUsers.put(pictureId, loginUser.getId());
            // 构造响应，发送加入编辑的消息通知
            PictureEditResponseMessage responseMessage = new PictureEditResponseMessage();
            responseMessage.setType(PictureEditMessageType.ENTER_EDIT.getValue());
            String msg = String.format("用户%s正在编辑图片",loginUser.getUserName());
            responseMessage.setMessage(msg);
            responseMessage.setUser(userService.getUserVO(loginUser));
            // 广播给所有用户
            broadcastPicture(pictureId, responseMessage);
        }
    }

    /**
     * 处理WebSocket连接关闭之后的操作
     * @param session 会话
     * @param status 状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        User loginUser = (User)session.getAttributes().get("user");
        Long pictureId = (Long)session.getAttributes().get("pictureId");
        // 移出当前用户的编辑状态
        handleExitEditMessage(null,session,loginUser,pictureId);
        // 删除会话
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (sessionSet != null) {
            sessionSet.remove(session);
            if (sessionSet.isEmpty()) {
                pictureSessions.remove(pictureId);
            }
        }
        // 构造响应，发送用户退出编辑的消息通知
        PictureEditResponseMessage responseMessage = new PictureEditResponseMessage();
        responseMessage.setType(PictureEditMessageType.INFO.getValue());
        String msg = String.format("用户%s退出了编辑",loginUser.getUserName());
        responseMessage.setMessage(msg);
        responseMessage.setUser(userService.getUserVO(loginUser));
        // 广播给所有用户
        broadcastPicture(pictureId, responseMessage);
    }

    /**
     * 广播图片编辑消息
     * @param pictureId 图片id
     * @param message 消息
     * @param exclude 排除的会话
     */
    private void broadcastPicture(Long pictureId, PictureEditResponseMessage message,WebSocketSession exclude) throws IOException {
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if(CollUtil.isNotEmpty(sessionSet)) {
            // 自定义序列化器，将Long类型转换为String类型，避免前端JSON解析错误
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(module);
            String json = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(json);
            for (WebSocketSession session : sessionSet) {
                if (!session.equals(exclude) && session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }

    /**
     * 广播图片编辑消息
     * @param pictureId 图片id
     * @param message 消息
     */
    private void broadcastPicture(Long pictureId, PictureEditResponseMessage message) throws IOException {
        broadcastPicture(pictureId, message, null);
    }
}
