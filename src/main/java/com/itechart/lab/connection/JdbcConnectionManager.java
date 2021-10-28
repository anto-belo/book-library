package com.itechart.lab.connection;

import com.itechart.lab.exception.ConnectionManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class JdbcConnectionManager implements ConnectionManager {
    private static final Logger logger = LogManager.getLogger(JdbcConnectionManager.class);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/book_library";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Endl3ssR0ad5";

    private static final String MSG_DRV_REG_SUCCESS = "Driver registration succeeded";
    private static final String MSG_DRV_REG_FAIL = "Driver registration failed";
    private static final String MSG_DRV_DEREG_SUCCESS = "Driver deregistration succeeded";
    private static final String MSG_DRV_DEREG_FAIL = "Driver deregistration failed";

    private static final String MSG_CONN_OPEN_SUCCESS = "Connection open succeeded";
    private static final String MSG_TMPL_CONN_OPEN_FAIL = "Connection open failed (%s)";
    private static final String MSG_CONN_CLOSE_SUCCESS = "Connection close succeeded";
    private static final String MSG_TMPL_CONN_CLOSE_FAIL = "Connection close failed (%s)";

    private static JdbcConnectionManager instance;

    private Connection connection;

    private JdbcConnectionManager() {
    }

    static JdbcConnectionManager getInstance() {
        if (instance == null) {
            instance = new JdbcConnectionManager();
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void init() {
        try {
            registerDrivers();
            logger.info(MSG_DRV_REG_SUCCESS);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            logger.info(MSG_CONN_OPEN_SUCCESS);
        } catch (ConnectionManagerException e) {
            logger.fatal(MSG_DRV_REG_FAIL);
            //todo abort app
        } catch (SQLException e) {
            logger.fatal(String.format(MSG_TMPL_CONN_OPEN_FAIL, e.getMessage()));
            //todo abort app
        }
    }

    @Override
    public void destroy() {
        try {
            connection.close();
            logger.info(MSG_CONN_CLOSE_SUCCESS);
            deregisterDrivers();
            logger.info(MSG_DRV_DEREG_SUCCESS);
        } catch (ConnectionManagerException e) {
            logger.error(MSG_DRV_DEREG_FAIL);
        } catch (SQLException e) {
            logger.error(String.format(MSG_TMPL_CONN_CLOSE_FAIL, e.getMessage()));
        }
    }

    private void registerDrivers() throws ConnectionManagerException {
        try {
            new com.mysql.cj.jdbc.Driver();
//            DriverManager.registerDriver(DriverManager.getDriver(DB_URL)); //todo remove if not required
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ConnectionManagerException();
        }
    }

    private void deregisterDrivers() throws ConnectionManagerException {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new ConnectionManagerException();
            }
        }
    }
}
