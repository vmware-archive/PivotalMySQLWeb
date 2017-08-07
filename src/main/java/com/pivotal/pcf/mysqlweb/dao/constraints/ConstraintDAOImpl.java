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
package com.pivotal.pcf.mysqlweb.dao.constraints;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

public class ConstraintDAOImpl implements ConstraintDAO
{
    protected static Logger logger = Logger.getLogger(ConstraintDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(javax.sql.DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Constraint> retrieveConstraintList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        List<Constraint>       constraints = null;
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

            constraints = jdbcTemplate.query(Constants.USER_CONSTRAINTS, new Object[]{schema, srch}, new ConstraintMapper());
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all constraints with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }


        return constraints;
    }

    @Override
    public Result simpleconstraintCommand(String schemaName, String constraintName, String tableName, String contraintType, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = new Result();

        SingleConnectionDataSource dataSource = null;

        if (type != null)
        {
            if (type.equalsIgnoreCase("DROP")) {
                if (contraintType.equalsIgnoreCase("UNIQUE")) {
                    command = String.format(Constants.DROP_CONSTRAINT_UNIQUE, schemaName, tableName, constraintName);
                } else if (contraintType.equalsIgnoreCase("FOREIGN KEY")) {
                    command = String.format(Constants.DROP_CONSTRAINT_FK, schemaName, tableName, constraintName);
                } else if (contraintType.equalsIgnoreCase("PRIMARY KEY")) {
                    command = String.format(Constants.DROP_CONSTRAINT_PRIMARY_KEY, schemaName, tableName);
                } else {
                    // not really expecting anything here
                }
            }
        }

        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();
        res = genericDAO.runCommand(command, userKey);

        return res;
    }

}
