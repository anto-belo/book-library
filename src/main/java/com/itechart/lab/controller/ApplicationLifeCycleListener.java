package com.itechart.lab.controller;

import com.itechart.lab.connection.ConnectionManager;
import com.itechart.lab.mail.Mailer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationLifeCycleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionManager.getManager().init();
        Mailer.getInstance().init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.getManager().destroy();
        Mailer.getInstance().destroy();
    }
}
