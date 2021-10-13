package com.itechart.lab.connection;

import java.sql.Connection;

public interface ConnectionManager {
    Connection getConnection();

    void init();

    void destroy();

    static ConnectionManager getManager() {
        return JdbcConnectionManager.getInstance();
    }
}
