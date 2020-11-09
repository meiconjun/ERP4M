package org.meiconjun.erp4m.base;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: controller拦截器（代理）
 * @date 2020/10/22 21:27
 */
public class ControllerInterceptor implements MethodInterceptor {
    /**
     *
     * @param o 被代理的对象
     * @param method 被拦截的方法
     * @param objects 方法入参
     * @param methodProxy 用于调用原始方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }
}
