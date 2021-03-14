package org.meiconjun.erp4m;

import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 启动类
 * @date 2021/2/1 22:27
 */
@SpringBootApplication
public class Erp4mApplication {
    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = SpringApplication.run(Erp4mApplication.class, args);
            SpringContextHolder.setApplicationContext(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 拦截.do请求
     * @param dispatcherServlet
     * @return
     */
    /*@Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletRegistrationBean.addUrlMappings("*.do");
        return  servletRegistrationBean;
    }*/
}
