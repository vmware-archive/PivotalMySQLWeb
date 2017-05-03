package com.pivotal.pcf.mysqlweb.utils;

import org.apache.commons.dbcp2.BasicDataSource;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPConnectionPool
{
    protected static Logger logger = Logger.getLogger(DBCPConnectionPool.class);
    private static BasicDataSource dataSource;
    private static DBCPConnectionPool instance = null;

    static
    {
        instance = new DBCPConnectionPool();
    }

    private DBCPConnectionPool()
    {
        // Exists only to defeat instantiation.
    }

    public static DBCPConnectionPool getInstance() throws Exception
    {
        return instance;
    }

    public static void setInstancePoolDetails(String username, String password, String url) throws Exception
    {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        ds.setValidationQuery("SELECT 1");
        ds.setMaxTotal(20);
        ds.setInitialSize(1);
        ds.setMaxIdle(0);

        dataSource = ds;

        logger.info("p-msql database connection pool parameters have been defined and pool created");
    }

    public Connection getConnection() throws SQLException
    {

        return dataSource.getConnection();
    }
}
