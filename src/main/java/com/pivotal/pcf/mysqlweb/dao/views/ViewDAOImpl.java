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
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.JDBCUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewDAOImpl implements ViewDAO
{
    protected static Logger logger = Logger.getLogger(ViewDAOImpl.class);

    @Override
    public List<View> retrieveViewList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        List<View>        views = null;
        String            srch = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);

            stmt = conn.prepareStatement(Constants.USER_VIEWS);
            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";

            stmt.setString(1, schema);
            stmt.setString(2, srch);
            rset = stmt.executeQuery();

            views = makeViewListFromResultSet(rset);
        }
        catch (SQLException se)
        {
            logger.info("Error retrieving all views with search string = " + search);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all views with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return views;
    }

    @Override
    public Result simpleviewCommand(String schemaName, String viewName, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = null;

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

        res = PivotalMySQLWebDAOUtil.runCommand(command, userKey);

        return res;
    }

    @Override
    public String getViewDefinition(String schemaName, String viewName, String userKey) throws PivotalMySQLWebException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet         rset = null;
        String            def = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.prepareStatement(Constants.USER_VIEW_DEF);
            stmt.setString(1, schemaName);
            stmt.setString(2, viewName);
            rset = stmt.executeQuery();

            rset.next();

            def = rset.getString(1);

        }
        catch (SQLException se)
        {
            logger.info("Error retrieving view definition for view = " + viewName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving view definition for view = " + viewName);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return def;

    }

    private List<View> makeViewListFromResultSet (ResultSet rset) throws SQLException
    {
        List<View> views = new ArrayList<View>();

        while (rset.next())
        {
            View view = new View(rset.getString(1),
                    rset.getString(2),
                    rset.getString(3));
            views.add(view);
        }

        return views;

    }
}
