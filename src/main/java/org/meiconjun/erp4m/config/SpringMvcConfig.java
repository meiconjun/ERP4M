package org.meiconjun.erp4m.config;

import org.meiconjun.erp4m.interceptor.RequestHolder;
import org.meiconjun.erp4m.interceptor.UserSessionInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: mvc相关配置
 * @date 2021/1/31 22:03
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Resource
    private CustomConfigProperties customConfigProperties;

    /**
     * 配置映射处理
     */
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }
    /**
     * 配置适配器
     */
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }
    /**
     * 文件上传支持
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        HashMap<String, Object> custom = customConfigProperties.getCustom();
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize((long)custom.get("maxUploadSize"));
        commonsMultipartResolver.setMaxInMemorySize((int)custom.get("maxInMemorySize"));
        commonsMultipartResolver.setDefaultEncoding((String) custom.get("defaultEncoding"));
        return commonsMultipartResolver;
    }
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserSessionInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new RequestHolder()).addPathPatterns("/**");
    }

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean characterEncodingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        filterRegistrationBean.setFilter(characterEncodingFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setName("characterEncodingFilter");
        return filterRegistrationBean;
    }
    /**
     * 欢迎页
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:login.html");
    }
}
