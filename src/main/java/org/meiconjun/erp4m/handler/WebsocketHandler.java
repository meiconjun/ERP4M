package org.meiconjun.erp4m.handler;

import org.meiconjun.erp4m.bean.WebSocketUser;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 消息处理
 * @date 2020/4/16 21:43
 */
public class WebsocketHandler extends TextWebSocketHandler {
    protected static Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    public static WebSocketUser users = new WebSocketUser();

    public WebsocketHandler() {

    }

    /**
     * 连接建立后的处理
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userNo = (String) session.getAttributes().get("userNo");

        if (CommonUtil.isStrBlank(userNo)) {
            long id = users.getWebSocketId();
            logger.info("生成随机UserId：{}", id);
            users.putUserNoAndWebSocketSession(String.valueOf(id), session);
//            session.sendMessage(new TextMessage("连接建立成功，userNo：" + userNo));
        } else {
            logger.info("userNo：{}" + userNo);
            users.putUserNoAndWebSocketSession(userNo, session);
//            session.sendMessage(new TextMessage("连接建立成功，userNo：" + userNo));
        }
        // 读取离线消息，推送用户
        List<TextMessage> message = WebsocketHandler.getOfflineMsg(userNo);
        if (message != null && !message.isEmpty()) {
            for (int i = 0; i < message.size(); i++) {
                WebsocketHandler.sendMessageToUser(userNo, message.get(i));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("接收到websocketmsg：\n" + message.toString());
        super.handleTextMessage(session, message);
        String msg = message.getPayload();//? 获取原始消息？
        logger.info("websocketmsg payload：\n" + msg);
        if (!CommonUtil.isStrBlank(msg)) {
            String param[] = msg.split("\\|");//TODO 待修改
            // 发送消息给所有用户
            String userNo = (String) session.getAttributes().get("userNo");
            //TODO 待修改↓
            sendMessageToUser(param[1], new TextMessage(param[0] + "|" + userNo + "|" + param[2]));
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接断开后移除session
        users.removeWebSocketSession(session);
    }

    /**
     * 给用户发送消息
     * @param userNo
     * @param message
     */
    public static void sendMessageToUser(String userNo, TextMessage message) {
        List<WebSocketSession> webUsers = users.getWebSocketSessionByUserNo(userNo);

        if (webUsers == null || webUsers.size() == 0) {
//            users.putOfflineMsg(userNo, message);
            //TODO 把消息存表
            return;
        }
        for (WebSocketSession wss : webUsers) {
            try {
                if (wss.isOpen()) {
                    wss.sendMessage(message);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 给用户发送消息
     * @param userNo
     * @param message
     */
    public static void sendBinaryMessageToUser(String userNo, BinaryMessage message) {
        List<WebSocketSession> webUsers = users.getWebSocketSessionByUserNo(userNo);
        if (webUsers == null || webUsers.isEmpty()) {
            //TODO 把消息存表
            return;
        }
        for (WebSocketSession wss : webUsers) {
            try {
                if (wss.isOpen()) {
                    wss.sendMessage(message);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    /**
     * 获取用户的离线消息
     * @param userNo
     * @return
     */
    public static List<TextMessage> getOfflineMsg(String userNo) {
        return users.getOfflineMsgByUserNo(userNo);
    }
}
