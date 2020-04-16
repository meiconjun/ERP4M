package org.meiconjun.erp4m.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.meiconjun.erp4m.interceptor.WebSocketInterceptor;
import org.meiconjun.erp4m.handler.WebsocketHandler;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: WebSocket监听Url
 * @date 2020/4/15 22:26
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class SpringWebSocketListener implements WebMvcConfigurer, WebSocketConfigurer {
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/websocket").addInterceptors(new WebSocketInterceptor());
        registry.addHandler(webSocketHandler(), "/sockjs").addInterceptors(new WebSocketInterceptor()).withSockJS();
    }

    @Bean
    public TextWebSocketHandler webSocketHandler() {
        return new WebsocketHandler();
    }
}
