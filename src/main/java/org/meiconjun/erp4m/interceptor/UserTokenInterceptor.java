package org.meiconjun.erp4m.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.meiconjun.erp4m.annotation.PassToken;
import org.meiconjun.erp4m.annotation.UserLoginToken;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.meiconjun.erp4m.util.LogUtil;
import org.meiconjun.erp4m.util.TokenUtil;
import org.slf4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 用户Token验证
 * @date 2021/9/17 15:59
 */
public class UserTokenInterceptor implements HandlerInterceptor {
    private Logger platformLogger = LogUtil.getPlatformLogger();
    @Resource
    private UserConfigDao userConfigDao;

    private String[] except_url = {"login.html", "login.do"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        platformLogger.info("UserTokenInterceptor.....");
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
            platformLogger.info("本地请求不需拦截,URI[" + uri + "]");
        } else {
            String token = request.getHeader("token");
            // 如果不是映射到方法直接通过
            if (!(handler instanceof HandlerMethod)) {
                passFlag = true;
            } else {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                // 检查是否有passToken注解，有则不验证
                if (method.isAnnotationPresent(PassToken.class)) {
                    PassToken passToken = method.getAnnotation(PassToken.class);
                    if (passToken.required()) {
                        return true;
                    }
                }
                // 检查有没有需要用户权限的注解
                if (method.isAnnotationPresent(UserLoginToken.class)) {
                    UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
                    if (userLoginToken.required()) {
                        // token认证
                        if (StrUtil.isBlank(token)) {
                            throw new RuntimeException("无效token，请重新登陆");
                        }
                        // 获取token user_no
                        String user_no;
                        try {
                            user_no = JWT.decode(token).getAudience().get(0);
                        } catch (JWTDecodeException e) {
                            return false;
                        }
                        User user = userConfigDao.selectUserByNo(user_no);
                        if (user == null) {
                            throw new RuntimeException("用户不存在，请重新登陆");
                        }
                        // 验证
                        passFlag = TokenUtil.verifyToken(token);
                    }
                } else {
                    passFlag = true;
                }
            }
        }
        return passFlag;
    }
}
