/*
PivotalMySQLWeb

Copyright (c) 2017-Present Pivotal Software, Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
     * Get connection from ConnectionManager conList Map or the DBCP
     */
    static public Connection getConnection(String userKey) throws Exception
    {
        Connection conn = null;
        ConnectionManager cm = null;
        DBCPConnectionPool dbcp = null;

        if (userKey.startsWith("POOLED-CONNECTION-"))
        {
            dbcp = DBCPConnectionPool.getInstance();
            conn = dbcp.getConnection();
        }
        else
        {
            cm = ConnectionManager.getInstance();
            conn = cm.getConnection(userKey);
        }


        return conn;
    }

    static public void closePooledConnection(String userKey, Connection conn)
    {
        DBCPConnectionPool dbcp = null;

        if (userKey.startsWith("POOLED-CONNECTION-"))
        {
            try
            {
                conn.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    static public Map<String, String> getSchemaMap ()
    {
        Map<String, String> schemaMap = new HashMap<String, String>();

        schemaMap.put("Table", "0");
        schemaMap.put("View", "0");
        schemaMap.put("Index", "0");
        schemaMap.put("Constraint", "0");

        return schemaMap;
    }
}