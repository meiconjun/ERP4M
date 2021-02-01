package org.meiconjun.erp4m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
public class erp4mApplication {
    public static void main(String[] args) {
        SpringApplication.run(erp4mApplication.class, args);
    }

    /**
     * 拦截.do请求
     * @param dispatcherServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletRegistrationBean.addUrlMappings("*.do");
        return  servletRegistrationBean;
    }
}
