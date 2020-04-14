package org.meiconjun.erp4m.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 将本次请求的request对象存储到ThreadLocal中,后续通过get()方法直接获取
 * @date 2020/4/14 22:42
 */
public class RequestHolder extends HandlerInterceptorAdapter {

    private static ThreadLocal<HttpServletRequest> httpServletRequestHolder = new ThreadLocal<HttpServletRequest>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        httpServletRequestHolder.set(request);// 绑定到当前线程
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        httpServletRequestHolder.remove();// 清理资源引用
    }

    public static HttpServletRequest getHttpServletRequest () {
        return httpServletRequestHolder.get();
    }
}
