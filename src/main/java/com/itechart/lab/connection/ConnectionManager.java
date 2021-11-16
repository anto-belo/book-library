package com.itechart.lab.connection;

import com.itechart.lab.exception.ConnectionManagerException;

import java.sql.Connection;

public interface ConnectionManager {
    static ConnectionManager getManager() {
        return JdbcConnectionManager.getInstance();
    }

    Connection getConnection();

    boolean isRunning();

    void init();

    void destroy();

    void setAutoCommit(boolean autoCommit) throws ConnectionManagerException;

    void commit() throws ConnectionManagerException;

    void rollback() throws ConnectionManagerException;
}
