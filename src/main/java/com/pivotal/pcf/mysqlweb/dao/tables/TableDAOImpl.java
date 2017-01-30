package com.pivotal.pcf.mysqlweb.dao.tables;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.JDBCUtil;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import org.apache.log4j.Logger;

import javax.servlet.jsp.jstl.sql.ResultSupport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAOImpl implements TableDAO
{
    protected static Logger logger = Logger.getLogger("controller");

    @Override
    public List<Table> retrieveTableList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        List<Table>       tbls = null;
        String            srch = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);

            stmt = conn.prepareStatement(Constants.USER_TABLES);

            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";


            stmt.setString(1, schema);
            stmt.setString(2, srch);
            rset = stmt.executeQuery();

            tbls = makeTableListFromResultSet(rset);
        }
        catch (SQLException se)
        {
            logger.info("Error retrieving all tables with search string = " + search);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all tables with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return tbls;
    }

    @Override
    public Result simpletableCommand(String schemaName, String tableName, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = null;

        if (type != null)
        {
            if (type.equalsIgnoreCase("DROP"))
            {
                if (schemaName.equalsIgnoreCase("public"))
                {
                    command = String.format(Constants.DROP_TABLE_PUBLIC, tableName);
                }
                else
                {
                    command = String.format(Constants.DROP_TABLE, schemaName, tableName);
                }
            }
            else if (type.equalsIgnoreCase("EMPTY"))
            {
                if (schemaName.equalsIgnoreCase("public"))
                {
                    command = String.format(Constants.TRUNCATE_TABLE_PUBLIC, tableName);
                }
                else
                {
                    command = String.format(Constants.TRUNCATE_TABLE, schemaName, tableName);
                }
            }

        }

        res = PivotalMySQLWebDAOUtil.runCommand(command, userKey);

        return res;
    }

    @Override
    public javax.servlet.jsp.jstl.sql.Result getTableStructure(String schema, String tableName, String userKey) throws PivotalMySQLWebException
    {
        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rset = null;
        javax.servlet.jsp.jstl.sql.Result res = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.prepareStatement(Constants.USER_TAB_COLUMNS);
            stmt.setString(1, schema);
            stmt.setString(2, tableName);
            rset = stmt.executeQuery();

            res = ResultSupport.toResult(rset);

        }
        catch (SQLException se)
        {
            logger.info("Error retrieving table structure for table " + tableName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex) {
            logger.info("Error retrieving table structure for table  " + tableName);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return res;
    }

    @Override
    public javax.servlet.jsp.jstl.sql.Result getTableDetails (String schema, String tableName, String userKey) throws PivotalMySQLWebException
    {
        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rset = null;
        javax.servlet.jsp.jstl.sql.Result res = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.prepareStatement(Constants.TABLE_DETAILS);
            stmt.setString(1, schema);
            stmt.setString(2, tableName);
            rset = stmt.executeQuery();

            res = ResultSupport.toResult(rset);

        }
        catch (SQLException se)
        {
            logger.info("Error retrieving table details for table " + tableName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex) {
            logger.info("Error retrieving table details for table  " + tableName);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return res;
    }

    @Override
    public String runShowQuery (String schema, String tableName, String userKey) throws PivotalMySQLWebException
    {
        String queryData = null;
        Connection        conn = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            queryData = QueryUtil.runShowQuery(conn, String.format(Constants.CREATE_TABLE_QUERY, schema, tableName));
        }
        catch (SQLException se)
        {
            logger.info("Error running runShowQuery table details for table " + tableName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex) {
            logger.info("Error running runShowQuery table details for table  " + tableName);
            throw new PivotalMySQLWebException(ex);
        }

        return queryData;
    }

    public javax.servlet.jsp.jstl.sql.Result showIndexes(String schema, String tableName, String userKey) throws PivotalMySQLWebException
    {
        javax.servlet.jsp.jstl.sql.Result tableIndexes = null;
        Connection        conn = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            tableIndexes = QueryUtil.runQuery(conn, String.format(Constants.SHOW_INDEXES, schema, tableName), -1);
        }
        catch (SQLException se)
        {
            logger.info("Error running index query for table " + tableName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error running index query for table  " + tableName);
            throw new PivotalMySQLWebException(ex);
        }

        return tableIndexes;
    }

    private List<Table> makeTableListFromResultSet (ResultSet rset) throws SQLException
    {
        List<Table> tbls = new ArrayList<Table>();

        while (rset.next())
        {
            Table table = new Table(rset.getString(1),
                                    rset.getString(2),
                                    rset.getString(3),
                                    rset.getString(4));

            tbls.add(table);
        }

        return tbls;

    }
}
