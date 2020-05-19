package org.meiconjun.erp4m.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 将容器上下文存为静态变量，方便获取
 * @date 2020/5/19 22:19
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    //上下文
    private static ApplicationContext applicationContext = null;
    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    /**
     * 获取存储在静态变量中的ApplicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext () {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 从ApplicationContext中取得bean，自动转型为所赋值对象的类型
     */
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 清除applicationContext
     */
    public static void clearHolder() {
        logger.info("清除SpringContextHolder中的ApplicationContext:"
                + applicationContext);
        applicationContext = null;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextHolder.applicationContext != null) {
            logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:" + SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = applicationContext;
    }

    private static void assertContextInjected() {
        if (applicationContext == null) {
            String msg = "applicaitonContext属性未注入, 请在applicationContext" +
                    ".xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
