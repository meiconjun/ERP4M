package org.meiconjun.erp4m.bean;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 使用内存存储websocket在线用户
 * @SourceUrl: https://www.jianshu.com/p/501cd6699dcd
 * @date 2020/4/15 22:59
 */
public class WebSocketUser {
    /** 用户的socketSession集合，若一个用户在多设备登录时会有多个session **/
    private static Map<String, List<WebSocketSession>> userNoWebsession = null;
    /** 可根据socketSession获取用户号 **/
    private static Map<WebSocketSession, String> webSessionUserNo = null;
    /** 离线消息集合，考虑到内存存储可能会丢失，离线消息应用数据库存储 **/
    private static Map<String, List<TextMessage>> userNoWithOfflineMsg = null;

    private static long webSocketId = 100000000L;
    public WebSocketUser() {
        userNoWebsession = new ConcurrentHashMap<String,List<WebSocketSession>>();
        webSessionUserNo = new ConcurrentHashMap<WebSocketSession, String>();
        userNoWithOfflineMsg = new ConcurrentHashMap<String, List<TextMessage>>();
    }

    public static Map<String, List<WebSocketSession>> getUserNoWebsession() {
        return userNoWebsession;
    }

    public static void setUserNoWebsession(Map<String, List<WebSocketSession>> userNoWebsession) {
        WebSocketUser.userNoWebsession = userNoWebsession;
    }

    public static Map<WebSocketSession, String> getWebSessionUserNo() {
        return webSessionUserNo;
    }

    public static void setWebSessionUserNo(Map<WebSocketSession, String> webSessionUserNo) {
        WebSocketUser.webSessionUserNo = webSessionUserNo;
    }

    public static Map<String, List<TextMessage>> getUserNoWithOfflineMsg() {
        return userNoWithOfflineMsg;
    }

    public static void setUserNoWithOfflineMsg(Map<String, List<TextMessage>> userNoWithOfflineMsg) {
        WebSocketUser.userNoWithOfflineMsg = userNoWithOfflineMsg;
    }

    /**
     * 根据用户号获取WebSocketSession列表
     * @param userNo
     * @return
     */
    public List<WebSocketSession> getWebSocketSessionByUserNo(String userNo) {
        return userNoWebsession.get(userNo);
    }

    /**
     * 移除失效的session
     * @param webSocketSession
     */
    public synchronized void removeWebSocketSession(WebSocketSession webSocketSession) {
        if (webSocketSession == null) {
            return;
        }
        String userNo = webSessionUserNo.get(webSocketSession);
        webSessionUserNo.remove(webSocketSession);
        List<WebSocketSession> webSession = userNoWebsession.get(userNo);
        if (webSession == null) {
            return;
        }
        webSession.remove(webSocketSession);// 移除失效session
    }

    /**
     * 存放离线消息
     * @param userNo
     * @param msg
     */
    public synchronized void putOfflineMsg(String userNo, TextMessage msg) {
        if (userNoWithOfflineMsg.get(userNo) == null) {
            List<TextMessage> msgList = new ArrayList<TextMessage>();
            msgList.add(msg);
            userNoWithOfflineMsg.put(userNo, msgList);
        } else {
            List<TextMessage> msgList = userNoWithOfflineMsg.get(userNo);
            msgList.add(msg);
        }
    }

    /**
     * 根据用户号拿离线消息
     * @param userNo
     * @return
     */
    public List<TextMessage> getOfflineMsgByUserNo(String userNo) {
        return userNoWithOfflineMsg.get(userNo);
    }

    /**
     * 存放用户号和websocketsession
     * @param userNo
     * @param webSocketSession
     */
    public synchronized void putUserNoAndWebSocketSession(String userNo, WebSocketSession webSocketSession) {
        webSessionUserNo.put(webSocketSession, userNo);
        if (userNoWebsession.get(userNo) == null) {
            List<WebSocketSession> webSession = new ArrayList<WebSocketSession>();
            webSession.add(webSocketSession);
            userNoWebsession.put(userNo, webSession);
        } else {
            List<WebSocketSession> webSession = userNoWebsession.get(userNo);
            webSession.add(webSocketSession);
        }
    }

    /**
     * 生成一个webSocketId
     * @return
     */
    public synchronized long getWebSocketId() {
        return ++webSocketId;
    }


}
