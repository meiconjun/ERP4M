package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.bean.RequestBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: websocket发送消息工具类
 * @date 2020/5/19 16:57
 */
public class WebsocketMsgUtil {
    public static boolean sendMsgToUser(String userNo, RequestBean requestBean) {
        if (CommonUtil.isStrBlank(userNo)) {
            return false;
        }
        return true;
    }
}
