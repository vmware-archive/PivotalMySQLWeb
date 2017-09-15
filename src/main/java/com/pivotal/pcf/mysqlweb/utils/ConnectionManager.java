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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.pivotal.pcf.mysqlweb.beans.Login;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class ConnectionManager
{

    protected static Logger logger = Logger.getLogger(ConnectionManager.class);
    private Map<String,MysqlConnection> conList = new HashMap<String,MysqlConnection>();
    private Map<String,SingleConnectionDataSource> dsList = new HashMap<String,SingleConnectionDataSource>();
    private static ConnectionManager instance = null;
    private static DataSource cfDataSource = null;

    static
    {
        instance = new ConnectionManager();
    }

    private ConnectionManager()
    {
        // Exists only to defeat instantiation.
    }

    public static ConnectionManager getInstance() throws Exception
    {
        return instance;
    }

    public void setupCFDataSource (Login login) throws Exception
    {
        if (cfDataSource == null)
        {
            cfDataSource = AdminUtil.getDriverManagerDataSourceForCF(login);
            logger.info(" CF DataSource created for all users");
        }

    }

    public void addConnection (MysqlConnection conn, String key)
    {
        conList.put(key, conn);
        logger.info("Connection added with key " + key);
    }

    public void addDataSourceConnection (SingleConnectionDataSource dataSource, String key)
    {
        dsList.put(key, dataSource);
        logger.info("SingleConnectionDataSource added with key " + key);
    }

    public javax.sql.DataSource getDataSource (String key)
    {
        try
        {
            if (getCfDataSource() != null)
            {
                return getCfDataSource();
            }
            else
            {
                return dsList.get(key);
            }
        }
        catch (Exception ex)
        {
            logger.info("Unable to retrieve DataSource : " + ex.getMessage());
            return null;
        }

    }

    public DataSource getCfDataSource ()
    {
        return cfDataSource;
    }

    public void removeDataSource(String key) throws SQLException
    {
        if (dsList.containsKey(key))
        {
            SingleConnectionDataSource ds = dsList.get(key);
            ds.destroy();
            dsList.remove(key);
            conList.remove(key);
            logger.info("SingleConnectionDataSource removed with key " + key);
        }
        else
        {
            logger.info("No SingleConnectionDataSource with key " + key + " exists");
        }
    }

    public Map <String,MysqlConnection> getConnectionMap()
    {
        return conList;
    }

    public int getConnectionListSize ()
    {
        return conList.size();
    }

}
