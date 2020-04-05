package org.meiconjun.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class applicationListener implements ServletContextListener,ServletContextAttributeListener {

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
        System.getProperties().remove("log4jDir");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        //在环境变量中添加日志根目录路径
        String log4jDir = servletContextEvent.getServletContext().getRealPath("/");
        System.setProperty("log4jDir", log4jDir);
    }
}
