package org.meiconjun.erp4m.interceptor;

import org.meiconjun.erp4m.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 对websocket连接进行拦截过滤，可以对连接前和连接后自定义处理
 * @date 2020/4/16 21:10
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    private Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        logger.info("---------------before handshake----------------");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);//false-获取session为空时不自动创建，返回null
            if (session != null) {
                User user = (User) session.getAttribute("USER_SESSION");
                if (user != null) {
                    String userNo = user.getUser_no();
                    attributes.put("userNo", userNo); // 放进wesocketSession中
                } else {
                    logger.error("websocket连接加入失败，未获取到用户session");
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        logger.info("--------------after handshake--------------");
    }
}
