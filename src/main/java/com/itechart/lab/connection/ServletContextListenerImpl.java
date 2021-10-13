package com.itechart.lab.connection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionManager.getManager().init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.getManager().destroy();
    }
}
