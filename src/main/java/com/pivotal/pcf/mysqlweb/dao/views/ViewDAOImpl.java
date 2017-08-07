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
package com.pivotal.pcf.mysqlweb.dao.views;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

public class ViewDAOImpl implements ViewDAO
{
    protected static Logger logger = Logger.getLogger(ViewDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(javax.sql.DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<View> retrieveViewList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        List<View> views;
        String srch;

        javax.sql.DataSource dataSource = null;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";

            views = jdbcTemplate.query(Constants.USER_VIEWS, new Object[]{schema, srch}, new ViewMapper());

        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all views with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }

        return views;
    }

    @Override
    public Result simpleviewCommand(String schemaName, String viewName, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = new Result();
        SingleConnectionDataSource dataSource = null;

        if (type != null)
        {
            if (type.equalsIgnoreCase("DROP"))
            {
                if (schemaName.equalsIgnoreCase("public"))
                {
                    command = String.format(Constants.DROP_VIEW_PUBLIC, viewName);
                }
                else
                {
                    command = String.format(Constants.DROP_VIEW, schemaName, viewName);
                }
            }
        }

        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();
        res = genericDAO.runCommand(command, userKey);

        return res;
    }

    @Override
    public String getViewDefinition(String schemaName, String viewName, String userKey) throws PivotalMySQLWebException
    {
        String            def;
        javax.sql.DataSource dataSource;

        try
        {
            dataSource = AdminUtil.getDataSource(userKey);
            setDataSource(dataSource);

            def = jdbcTemplate.queryForObject
                    (Constants.USER_VIEW_DEF, new Object[]{schemaName, viewName}, String.class);

        }
        catch (Exception ex)
        {
            logger.info("Error retrieving view definition for view = " + viewName);
            throw new PivotalMySQLWebException(ex);
        }

        return def;

    }

}
