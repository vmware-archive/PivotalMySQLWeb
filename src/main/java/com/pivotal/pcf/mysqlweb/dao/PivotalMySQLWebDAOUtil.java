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
package com.pivotal.pcf.mysqlweb.dao;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PivotalMySQLWebDAOUtil
{
    static public Result runCommand (String command, String userKey) throws PivotalMySQLWebException
    {
        Result res = new Result();
        Connection conn    = null;
        Statement stmt    = null;

        res.setCommand(command);

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.createStatement();

            stmt.execute(command);
            // no need to commit it's auto commit already as it's DDL statement.
            res.setCommand(command);
            res.setMessage("SUCCESS");
        }
        catch (SQLException se)
        {
            // we don't want to stop it running we just need the error
            res.setMessage(se.getMessage());
        }
        catch (Exception ex)
        {
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            JDBCUtil.close(stmt);
        }

        return res;
    }

    static public Result runStoredCommand (String command, String userKey) throws PivotalMySQLWebException
    {
        Result res = new Result();
        Connection        			conn    = null;
        PreparedStatement stmt    = null;

        res.setCommand(command);

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.prepareCall(command);
            stmt.execute();

            // no need to commit it's auto commit already as it's DDL statement.
            res.setCommand(command);
            res.setMessage("SUCCESS");
        }
        catch (SQLException se)
        {
            // we don't want to stop it running we just need the error
            res.setMessage(se.getMessage());
        }
        catch (Exception ex)
        {
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            JDBCUtil.close(stmt);
        }

        return res;
    }

    static public List<String> getAllSchemas (String userKey) throws PivotalMySQLWebException
    {
        List<String> schemas = new ArrayList<String>();
        Connection        conn    = null;
        Statement         stmt    = null;
        ResultSet rset = null;
        String          sql = "select schema_name from information_schema.schemata order by 1";

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sql);

            while (rset.next())
            {
                String schema = rset.getString(1);
                schemas.add(schema);
            }

        }
        catch (Exception ex)
        {
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            JDBCUtil.close(stmt);
        }

        return schemas;

    }
}
