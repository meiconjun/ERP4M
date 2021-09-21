package org.meiconjun.erp4m.interceptor;

import cn.hutool.core.util.StrUtil;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 将本次请求的User对象存储到ThreadLocal中,后续通过get()方法直接获取
 * @date 2020/4/14 22:42
 */
public class RequestHolder implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(RequestHolder.class);

//    private static ThreadLocal<HttpServletRequest> httpServletRequestHolder = new ThreadLocal<HttpServletRequest>();
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("RequestHolder....");
//        httpServletRequestHolder.set(request);// 绑定到当前线程
        if (userHolder.get() == null) {
            String usrStr = request.getHeader("user");
            if (StrUtil.isBlank(usrStr)) {
                throw new RuntimeException("登录信息失效，请重新登陆");
            }
            User user = (User) CommonUtil.jsonToObj(usrStr, User.class);
            userHolder.set(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        httpServletRequestHolder.remove();// 清理资源引用
        userHolder.remove();
    }

    public static User getUser() {
        return userHolder.get();
    }

    public static void setUser(User user) {
        userHolder.set(user);
    }
}
