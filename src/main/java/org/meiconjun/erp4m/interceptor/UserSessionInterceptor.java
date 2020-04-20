package org.meiconjun.erp4m.interceptor;

import org.meiconjun.erp4m.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Lch
 * @Title: UserSessionInterceptor
 * @Package org.meiconjun.interceptor
 * @Description: 用户Session拦截器
 * @date 2020/4/5 14:29
 */
public class UserSessionInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(UserSessionInterceptor.class);
    private String[] except_url = {"login.html", "login.do"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("UserSessionInterceptor.....");
        Boolean passFlag = false;
        //获取请求的URO：取出http:[ip]:[port]后剩下的
        String uri = request.getRequestURI();

        // 判断访问地址是否是不需要拦截的
        if (uri.endsWith(".css") || uri.endsWith(".json") || uri.endsWith(".js") || uri.endsWith(".jpg")
                || uri.endsWith(".png") || uri.endsWith(".ico") || uri.endsWith(".txt")) {
            passFlag = true;
        }
        for (String s : except_url) {
            if (uri.contains(s)) {
                passFlag = true;
            }
        }
        if (passFlag == true) {
            logger.info("本地请求不需拦截,URI[" + uri + "]");
        } else {
            // 获取session
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("USER_SESSION");
            if (user != null) {
                passFlag = true;
            } else {
                // 未登录
                logger.info("请求未登录，拦截,uri:[" + uri + "]");
                /*request.setAttribute("err_msg", "您未登录或登录已失效，请重新登录！");
                request.getRequestDispatcher("/WEB-INF/static/html/login.html").forward(request, response);*/
//                response.sendError(999);
                response.setStatus(999);
            }
        }
        return passFlag;
    }
}
