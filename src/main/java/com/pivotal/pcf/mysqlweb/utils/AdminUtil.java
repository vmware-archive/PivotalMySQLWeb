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

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminUtil
{

    static public SingleConnectionDataSource newSingleConnectionDataSource
            (String url,
             String username,
             String passwd)
    {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();

        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl(url);

        if (username != null)
            ds.setUsername(username);

        if (passwd != null)
            ds.setPassword(passwd);

        return ds;
    }


    /*
     * Get connection from ConnectionManager conList Map
     */
    static public Connection getConnection(String userKey) throws Exception
    {
        Connection conn = null;
        ConnectionManager cm = ConnectionManager.getInstance();
        conn = cm.getDataSource(userKey).getConnection();
        return conn;
    }

    /*
     * Get DataSource from ConnectionManager conList Map
     */
    static public SingleConnectionDataSource getDataSource(String userKey) throws Exception
    {
        SingleConnectionDataSource dataSource = null;
        ConnectionManager cm = null;

        cm = ConnectionManager.getInstance();
        dataSource = cm.getDataSource(userKey);

        return dataSource;
    }

}