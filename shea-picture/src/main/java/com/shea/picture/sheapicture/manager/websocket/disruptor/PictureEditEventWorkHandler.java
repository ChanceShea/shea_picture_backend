package com.shea.picture.sheapicture.manager.websocket.disruptor;

import cn.hutool.json.JSONUtil;
import com.lmax.disruptor.WorkHandler;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.manager.websocket.PictureEditHandler;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditMessageType;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditRequestMessage;
import com.shea.picture.sheapicture.manager.websocket.model.PictureEditResponseMessage;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 图片编辑事件处理器（消费者）
 * @author : Shea.
 * @since : 2026/4/28 08:49
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {

    private final PictureEditHandler pictureEditHandler;
    private final UserService userService;

    @Override
    public void onEvent(PictureEditEvent pictureEditEvent) throws Exception {
        Long pictureId = pictureEditEvent.getPictureId();
        PictureEditRequestMessage perm = pictureEditEvent.getPictureEditRequestMessage();
        String type = perm.getType();
        PictureEditMessageType pictureEditMessageType = PictureEditMessageType.getByValue(type);
        WebSocketSession session = pictureEditEvent.getWebSocketSession();
        User loginUser = pictureEditEvent.getUser();
        // 根据消息类型进行不同的处理
        switch (pictureEditMessageType) {
            case ENTER_EDIT:
                pictureEditHandler.handleEnterEditMessage(perm, session, loginUser, pictureId);
                break;
            case EXIT_EDIT:
                pictureEditHandler.handleExitEditMessage(perm, session, loginUser, pictureId);
                break;
            case EDIT_ACTION:
                pictureEditHandler.handleEditActionMessage(perm, session, loginUser, pictureId);
                break;
            default:
                PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
                pictureEditResponseMessage.setType(PictureEditMessageType.ERROR.getValue());
                pictureEditResponseMessage.setMessage("未知的消息类型：" + type);
                pictureEditResponseMessage.setUser(userService.getUserVO(loginUser));
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pictureEditResponseMessage)));
        }
    }
}
