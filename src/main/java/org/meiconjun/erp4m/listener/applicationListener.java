package org.meiconjun.erp4m.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class applicationListener implements ServletContextListener,ServletContextAttributeListener {
    private Logger logger = LoggerFactory.getLogger(applicationListener.class);

    @Override
    public void attributeAdded(ServletContextAttributeEvent servletContextAttributeEvent) {

    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {

    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {

    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //在环境变量中添加日志根目录路径
        String log4jDir = servletContextEvent.getServletContext().getRealPath("/");
        System.setProperty("log4jDir", log4jDir);
        //在环境变量中添加项目根路径
        String contextRootPath = servletContextEvent.getServletContext().getRealPath("/");
        logger.info("@@@@@@@@@@@@@@@@@contextRootPath：" + contextRootPath);
        System.setProperty("contextRootPath", contextRootPath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.getProperties().remove("log4jDir");
        System.getProperties().remove("contextRootPath");
    }
}
