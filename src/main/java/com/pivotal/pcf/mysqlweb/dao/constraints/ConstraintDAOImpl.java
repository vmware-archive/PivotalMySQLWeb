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
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.JDBCUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConstraintDAOImpl implements ConstraintDAO
{
    protected static Logger logger = Logger.getLogger(ConstraintDAOImpl.class);

    @Override
    public List<Constraint> retrieveConstraintList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        List<Constraint>       constraints = null;
        String            srch = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);

            stmt = conn.prepareStatement(Constants.USER_CONSTRAINTS);

            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";


            stmt.setString(1, schema);
            stmt.setString(2, srch);
            rset = stmt.executeQuery();

            constraints = makeConstraintListFromResultSet(rset);
        }
        catch (SQLException se)
        {
            logger.info("Error retrieving all constraints with search string = " + search);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all constraints with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return constraints;
    }

    @Override
    public Result simpleconstraintCommand(String schemaName, String constraintName, String tableName, String contraintType, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = null;

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

        res = PivotalMySQLWebDAOUtil.runCommand(command, userKey);

        return res;
    }

    private List<Constraint> makeConstraintListFromResultSet (ResultSet rset) throws SQLException
    {
        List<Constraint> constraints = new ArrayList<Constraint>();

        while (rset.next())
        {
            Constraint constraint =
                    new Constraint(rset.getString(1),
                    rset.getString(2),
                    rset.getString(3),
                    rset.getString(4),
                    rset.getString(5));

            constraints.add(constraint);
        }

        return constraints;

    }
}
