package com.pivotal.pcf.mysqlweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminUtil
{

    static public Connection getNewConnection
            (String url,
             String username,
             String password) throws SQLException, ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url,username,password);
        return conn;
    }

    static public Connection getNewConnection (String url) throws SQLException, ClassNotFoundException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    /*
     * Get connection from ConnectionManager conList Map
     */
    static public Connection getConnection(String userKey) throws Exception
    {
        Connection conn = null;
        ConnectionManager cm = null;

        cm = ConnectionManager.getInstance();
        conn = cm.getConnection(userKey);

        return conn;
    }

    static public Map<String, String> getSchemaMap ()
    {
        Map<String, String> schemaMap = new HashMap<String, String>();

        schemaMap.put("Table", "0");
        schemaMap.put("View", "0");

        return schemaMap;
    }
}