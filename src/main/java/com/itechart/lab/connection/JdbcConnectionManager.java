package com.itechart.lab.connection;

import com.itechart.lab.exception.ConnectionManagerException;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

@Log4j2
public class JdbcConnectionManager implements ConnectionManager {
    private static final JdbcConnectionManager instance = new JdbcConnectionManager();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/book_library";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Endl3ssR0ad5";

    private static final String MSG_DRV_REG_SUCCESS = "Driver registration succeeded";
    private static final String MSG_DRV_REG_FAIL = "Driver registration failed";
    private static final String MSG_DRV_DEREG_SUCCESS = "Driver deregistration succeeded";
    private static final String MSG_DRV_DEREG_FAIL = "Driver deregistration failed";

    private static final String MSG_CONN_OPEN_SUCCESS = "Connection open succeeded";
    private static final String MSG_CONN_OPEN_FAIL = "Connection open failed (%s)";
    private static final String MSG_CONN_CLOSE_SUCCESS = "Connection close succeeded";
    private static final String MSG_CONN_CLOSE_FAIL = "Connection close failed (%s)";

    private Connection connection;
    private boolean running;

    private JdbcConnectionManager() {
    }

    static JdbcConnectionManager getInstance() {
        return instance;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void init() {
        try {
            registerDrivers();
            log.info(MSG_DRV_REG_SUCCESS);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            log.info(MSG_CONN_OPEN_SUCCESS);
            running = true;
        } catch (ConnectionManagerException e) {
            log.fatal(e.getMessage());
        } catch (SQLException e) {
            log.fatal(String.format(MSG_CONN_OPEN_FAIL, e.getMessage()));
        }
    }

    @Override
    public void destroy() {
        try {
            connection.close();
            log.info(MSG_CONN_CLOSE_SUCCESS);
            deregisterDrivers();
            log.info(MSG_DRV_DEREG_SUCCESS);
            running = false;
        } catch (ConnectionManagerException e) {
            log.error(e.getMessage());
        } catch (SQLException e) {
            log.error(String.format(MSG_CONN_CLOSE_FAIL, e.getMessage()));
        }
    }

    private void registerDrivers() throws ConnectionManagerException {
        try {
            new com.mysql.cj.jdbc.Driver();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ConnectionManagerException(MSG_DRV_REG_FAIL);
        }
    }

    private void deregisterDrivers() throws ConnectionManagerException {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            } catch (SQLException e) {
                log.error(e.getMessage());
                throw new ConnectionManagerException(MSG_DRV_DEREG_FAIL);
            }
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws ConnectionManagerException {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new ConnectionManagerException(e.getMessage());
        }
    }

    @Override
    public void commit() throws ConnectionManagerException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new ConnectionManagerException(e.getMessage());
        }
    }

    @Override
    public void rollback() throws ConnectionManagerException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new ConnectionManagerException(e.getMessage());
        }
    }
}
