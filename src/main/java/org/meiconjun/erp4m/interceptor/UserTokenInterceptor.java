package org.meiconjun.erp4m.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.meiconjun.erp4m.annotation.PassToken;
import org.meiconjun.erp4m.annotation.UserLoginToken;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.LogUtil;
import org.meiconjun.erp4m.util.TokenUtil;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 用户Token验证
 * @date 2021/9/17 15:59
 */
public class UserTokenInterceptor implements HandlerInterceptor {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();
    private Logger platformLogger = LogUtil.getPlatformLogger();
    @Resource
    private UserConfigDao userConfigDao;

    private String[] except_url = {"login.html"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        platformLogger.info("UserTokenInterceptor.....");
        Boolean passFlag = false;
        //获取请求的URO：取出http:[ip]:[port]后剩下的
        String uri = request.getRequestURI();

        // 判断访问地址是否是不需要拦截的
        /*if (uri.endsWith(".css") || uri.endsWith(".json") || uri.endsWith(".js") || uri.endsWith(".jpg")
                || uri.endsWith(".png") || uri.endsWith(".ico") || uri.endsWith(".txt")) {
            passFlag = true;
        }*/
        if (uri.endsWith("/")) {
            request.getRequestDispatcher("/login.html").forward(request, response);
        }
        for (String s : except_url) {
            if (uri.contains(s)) {
                passFlag = true;
            }
        }
        if (passFlag == true) {
            platformLogger.info("本地请求不需拦截,URI[" + uri + "]");
        } else {
            String token = request.getHeader("authorization");
            // 如果不是映射到方法直接通过
            // (ResourceHttpRequestHandler，SpringMvc用来处理静态资源请求的handler。如果不instanceof，就会把js css各种文件也拦截掉)
            if (!(handler instanceof HandlerMethod)) {
                platformLogger.info("静态资源请求。。。。。。");
                /*ResourceHttpRequestHandler handler1 = (ResourceHttpRequestHandler)handler;
                List locations = handler1.getLocations();
                if (locations.size() == 1) {
                    ClassPathResource cpr = (ClassPathResource)locations.get(0);
                    platformLogger.info("getPath:" + cpr.getPath());
                    if ("static/".equals(cpr.getPath())) {
                        request.getRequestDispatcher("/login.html").forward(request, response);
                        return false;
                    }
                }*/
                passFlag = true;
            } else {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                Class clazz = handlerMethod.getBeanType();
                // 检查是否有passToken注解，有则不验证
                if (method.isAnnotationPresent(PassToken.class)) {
                    PassToken passToken = method.getAnnotation(PassToken.class);
                    if (passToken.required()) {
                        platformLogger.info("存在passToken注解,不验证");
                        return true;
                    }
                }
                // 检查有没有需要用户权限的注解
                if (clazz.isAnnotationPresent(UserLoginToken.class)) {
                    platformLogger.info("进行TOKEN验证@@@@@@@@@");
                    UserLoginToken userLoginToken = (UserLoginToken) clazz.getAnnotation(UserLoginToken.class);
                    if (userLoginToken.required()) {
                        platformLogger.info("进行TOKEN验证！！！！！！");
                        // token认证
                        if (StrUtil.isBlank(token)) {
                            throw new RuntimeException("无效token，请重新登陆");
                        }
                        // 获取token user_no
                        String user_no;
                        DecodedJWT decodedJWT;
                        try {
                            decodedJWT = JWT.decode(token);
                        } catch (JWTDecodeException e) {
                            return false;
                        }
                        User jwtUser = (User) CommonUtil.jsonToObj(decodedJWT.getClaim("user").asString(), User.class);
                        Date expireDate = decodedJWT.getExpiresAt();
                        if (expireDate.compareTo(new Date()) < 0) {
                            throw new RuntimeException("TOKEN已过期，请重新登录");
                        }
                        User user = userConfigDao.selectUserByNo(jwtUser.getUser_no());
                        if (user == null) {
                            throw new RuntimeException("用户不存在，请重新登陆");
                        }
                        // 验证
                        passFlag = TokenUtil.verifyToken(token);

                        /*String usrStr = request.getHeader("user");
                        if (StrUtil.isBlank(usrStr)) {
                            throw new RuntimeException("登录信息失效，请重新登陆");
                        }
                        User user = (User) CommonUtil.jsonToObj(usrStr, User.class);*/
                        userHolder.set(user);
                        // token延时
                        String token_new = TokenUtil.getToken(user, 3*600000);
                        response.setHeader("authorization", token_new);
                    }
                } else {
                    passFlag = true;
                }
            }
        }
        return passFlag;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userHolder.remove();
    }
    public static User getUser() {
        return userHolder.get();
    }

}
