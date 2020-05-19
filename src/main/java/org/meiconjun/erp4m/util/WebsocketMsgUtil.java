package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.meiconjun.erp4m.handler.WebsocketHandler;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.springframework.web.socket.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: websocket消息推送工具类
 * @date 2020/5/19 16:57
 */
public class WebsocketMsgUtil {

    private static UserConfigDao userConfigDao;

    static {
        // 在类加载时将bean注入
        userConfigDao = SpringContextHolder.getBean("userConfigDao");
    }
    /**
     * 消息推送
     * @param userNo
     * @param messageBean
     * @return
     */
    public static boolean sendMsgToUser(String userNo, MessageBean messageBean) {
        if (CommonUtil.isStrBlank(userNo)) {
            return false;
        }
        String msgStr = CommonUtil.objToJson(messageBean);
        WebsocketHandler.sendMessageToUser(userNo, new TextMessage(msgStr));
        return true;
    }

    /**
     * 给多个用户发送消息
     * @param userList
     * @param roleList
     * @param messageBean
     * @return
     */
    public static void sendMsgToMultipleUser(List<String> userList, List<String> roleList, MessageBean messageBean) {
        List<String> userNoList = new ArrayList<String >();// 存储要发送的用户号
        if (userList != null && userList.size() > 0) {
            userNoList.addAll(userList);
        }
        if (roleList != null && roleList.size() > 0) {
            for (String role_no: roleList) {
                List<String> tempUserList = userConfigDao.selectUsersByRole(role_no);
                for (String user_no : tempUserList) {
                    if (!userNoList.contains(user_no)) {
                        userNoList.add(user_no);
                    }
                }
            }
        }

        // 逐个推送
        for (String user_no : userNoList) {
            sendMsgToUser(user_no, messageBean);
        }
    }
}
