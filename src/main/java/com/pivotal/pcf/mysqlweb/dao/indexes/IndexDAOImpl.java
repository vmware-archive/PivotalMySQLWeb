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
package com.pivotal.pcf.mysqlweb.dao.indexes;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

public class IndexDAOImpl implements IndexDAO
{
    protected static Logger logger = Logger.getLogger(IndexDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(javax.sql.DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Index> retrieveIndexList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        List<Index>       indexes = null;
        String            srch = null;
        javax.sql.DataSource dataSource = null;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";

            indexes = jdbcTemplate.query(Constants.USER_INDEXES, new Object[]{schema, srch}, new IndexMapper());
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all indexes with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }

        return indexes;
    }

    @Override
    public Result simpleindexCommand(String schemaName, String indexName, String type, String tableName, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = new Result();
        SingleConnectionDataSource dataSource = null;

        if (type != null)
        {
            if (type.equalsIgnoreCase("DROP"))
            {
                if (indexName.equalsIgnoreCase("PRIMARY"))
                {
                    command = String.format(Constants.DROP_INDEX_PRIMARY, schemaName, tableName);
                }
                else
                {
                    command = String.format(Constants.DROP_INDEX, schemaName, tableName, indexName);
                }
            }

        }

        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();
        res = genericDAO.runCommand(command, userKey);

        return res;
    }

    @Override
    public WebResult getIndexDetails(String schema, String tableName, String indexName, String userKey) throws PivotalMySQLWebException
    {
        javax.sql.DataSource dataSource;
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();
        WebResult webResult;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            webResult = genericDAO.runGenericQuery
                    (Constants.INDEX_DETAILS, new Object[]{schema, tableName, indexName}, userKey, -1);

        }
        catch (Exception ex) {
            logger.info("Error retrieving index details for index " + indexName);
            throw new PivotalMySQLWebException(ex);
        }

        return webResult;
    }
}
