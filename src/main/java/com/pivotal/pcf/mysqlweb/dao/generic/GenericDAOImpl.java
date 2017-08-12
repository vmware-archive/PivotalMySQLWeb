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
package com.pivotal.pcf.mysqlweb.dao.generic;

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GenericDAOImpl implements GenericDAO
{
    protected static Logger logger = Logger.getLogger(GenericDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(javax.sql.DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public WebResult runGenericQuery (String sql, Object[] args, String userKey, int maxRows) throws PivotalMySQLWebException
    {
        javax.sql.DataSource dataSource = null;
        WebResult webResult = null;
        List<Map<String, Object>> resultList = null;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            if (maxRows != -1)
            {
                jdbcTemplate.setMaxRows(maxRows);
            }

            if (args == null) {
                resultList = jdbcTemplate.queryForList(sql);
            }
            else {
                resultList = jdbcTemplate.queryForList(sql, args);
            }

            String[] columnNames = null;
            // Get Column Names
            if (resultList.size() > 0) {
                Set<String> keySet = resultList.get(0).keySet();
                columnNames = keySet.toArray(new String[keySet.size()]);
            }

            webResult = new WebResult(columnNames, resultList);

        }
        catch (Exception ex)
        {
            logger.info("Error running generic query");
            throw new PivotalMySQLWebException(ex);
        }

        return webResult;
    }

    @Override
    public CommandResult runStatement(String sql, String elapsedTime, String ddl, String userKey) throws PivotalMySQLWebException {
        javax.sql.DataSource dataSource;
        CommandResult res = new CommandResult();

        try {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);
            res.setCommand(sql);

            long start = System.currentTimeMillis();

            if (ddl.equals("Y")) {
                jdbcTemplate.execute(sql);
                res.setRows(-1);
            }
            else {
                int rowsAffected  = jdbcTemplate.update(sql);
                res.setRows(rowsAffected);
            }

            long end = System.currentTimeMillis();

            double timeTaken = new Double(end - start).doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");

            // no need to commit it's auto commit already as it's DDL statement.
            res.setMessage("SUCCESS");

            if (elapsedTime.equals("Y"))
            {
                res.setElapsedTime("" + df.format(timeTaken));
            }

        }
        catch (Exception ex)
        {
            logger.info("Error running generic DML");
            res.setMessage("ERROR: " + ex.getMessage());
            res.setRows(-1);
        }

        return res;
    }

    @Override
    public Map<String, Long> populateSchemaMap(String schema, String userKey) throws PivotalMySQLWebException {
        javax.sql.DataSource dataSource;
        CommandResult res = new CommandResult();
        Map<String, Long> schemaMap = new HashMap<String, Long>();
        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            List<Map<String, Object>> rows = jdbcTemplate.queryForList
                    (Constants.SCHEMA_MAP_QUERY, new Object[]{schema, schema, schema, schema});

            schemaMap = Utils.getSchemaMap();

            for(Map result: rows)
            {
                schemaMap.put((String)result.get("object_type"), (Long)result.get("count(*)"));
            }

        }
        catch (Exception ex)
        {
            logger.info("Error populating schema map");
            throw new PivotalMySQLWebException(ex);
        }

        return schemaMap;
    }

    @Override
    public List<String> allSchemas(String userKey) throws PivotalMySQLWebException
    {
        javax.sql.DataSource dataSource;
        List<String> schemas;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            schemas = jdbcTemplate.queryForList(Constants.ALL_SCHEMAS,String.class);

        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all schemas");
            throw new PivotalMySQLWebException(ex);
        }

        return schemas;
    }

    @Override
    public Result runCommand (String sql, String userKey)
    {
        Result res = new Result();

        javax.sql.DataSource dataSource;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);
            res.setCommand(sql);
            jdbcTemplate.execute(sql);
            res.setMessage("SUCCESS");
        }
        catch (Exception ex)
        {
            res.setMessage(ex.getMessage());
        }

        return res;
    }

}
