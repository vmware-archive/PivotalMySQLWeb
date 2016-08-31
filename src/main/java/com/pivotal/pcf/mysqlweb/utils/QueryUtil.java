package com.pivotal.pcf.mysqlweb.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

import au.com.bytecode.opencsv.CSVWriter;
import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.dao.indexes.Constants;
import org.apache.log4j.Logger;
import org.jooq.impl.DSL;

public class QueryUtil
{
    protected static Logger logger = Logger.getLogger("controller");

    static public Result runExplainPlan (Connection conn, String query) throws SQLException
    {
        Statement stmt = null;
        String sql = "explain %s";
        ResultSet rset = null;
        Result    res   = null;

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(String.format(sql, query));

	       /*
	        * Convert the ResultSet to a
	        * Result object that can be used with JSTL tags
	        */

            res = ResultSupport.toResult(rset);
        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return res;
    }

    static public String runQueryForCSV (Connection conn, String query) throws SQLException, IOException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        StringWriter sw = new StringWriter();

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);

            CSVWriter csvWriter = new CSVWriter(sw, ',', '"', '\"');
            csvWriter.writeAll(rset, true);
            csvWriter.flush();

        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return sw.toString();
    }

    static public String runQueryForJSON (Connection conn, String query) throws SQLException, IOException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        String jsonResult = null;

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);

            jsonResult = DSL.using(conn).fetch(rset).formatJSON();

        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return jsonResult;
    }

    static public Result runQuery (Connection conn, String query, int maxrows) throws SQLException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        Result    res   = null;

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);

	      /*
	       * Convert the ResultSet to a
	       * Result object that can be used with JSTL tags
	       */
            if (maxrows == -1)
            {
                res = ResultSupport.toResult(rset);
            }
            else
            {
                res = ResultSupport.toResult(rset, maxrows);
            }
        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return res;
    }

    static public List<Result> runStoredprocWithResultSet
            (Connection conn, String query, int maxrows, int resultsets) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rset  = null;
        Result    res   = null;
        List<Result> results = new ArrayList<Result>();

        try
        {

            for (int i = 1; i <= resultsets; i++)
            {
                if (i == 1)
                {
                    pstmt = conn.prepareCall(query);
                    pstmt.execute();
                }
                else
                {
                    pstmt.getMoreResults();
                }

                rset = pstmt.getResultSet();
                res = null;

    	      /*
    	       * Convert the ResultSet to a
    	       * Result object that can be used with JSTL tags
    	       */
                if (maxrows == -1)
                {
                    res = ResultSupport.toResult(rset);
                }
                else
                {
                    res = ResultSupport.toResult(rset, maxrows);
                }

                results.add(res);

                rset.close();
                rset = null;
            }


        }
        finally
        {
            JDBCUtil.close(pstmt);
            JDBCUtil.close(rset);
        }

        return results;
    }

    static public int runQueryCount (Connection conn, String query) throws SQLException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        int count = 0;

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery("select count(*) from (" + query + ") as \"Count\"");
            rset.next();
            count = rset.getInt(1);
        }
        catch (SQLException se)
        {
            // do nothing if we can't get count.
        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return count;
    }

    static public CommandResult runCommitOrRollback (Connection conn, boolean commit, String elapsedTime) throws SQLException
    {
        CommandResult res = new CommandResult();

        try
        {
            long start = System.currentTimeMillis();

            if (commit)
            {
                res.setCommand("commit");
                conn.commit();
            }
            else
            {
                res.setCommand("rollback");
                conn.rollback();
            }

            long end = System.currentTimeMillis();
            double timeTaken = new Double(end - start).doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");

            if (elapsedTime.equals("Y"))
            {
                res.setElapsedTime("" + df.format(timeTaken));
            }

            res.setRows(0);
            res.setMessage("SUCCESS");

        }
        catch (SQLException se)
        {
            // we don't want to stop it running we just need the error
            res.setMessage("ERROR: " + se.getMessage());
            res.setRows(-1);
        }

        return res;

    }

    static public CommandResult runCommand (Connection conn, String command, String elapsedTime) throws SQLException
    {
        CommandResult res = new CommandResult();

        Statement         stmt    = null;

        res.setCommand(command);

        try
        {
            stmt = conn.createStatement();

            long start = System.currentTimeMillis();
            int rowsAffected = stmt.executeUpdate(command);
            long end = System.currentTimeMillis();

            double timeTaken = new Double(end - start).doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");

            res.setRows(rowsAffected);

            // no need to commit it's auto commit already as it's DDL statement.
            res.setCommand(command);
            res.setMessage("SUCCESS");

            if (elapsedTime.equals("Y"))
            {
                res.setElapsedTime("" + df.format(timeTaken));
            }
        }
        catch (SQLException se)
        {
            // we don't want to stop it running we just need the error
            res.setMessage("ERROR: " + se.getMessage());
            res.setRows(-1);
        }
        finally
        {
            JDBCUtil.close(stmt);
        }

        return res;
    }

    static public Map<String, String> populateSchemaMap(Connection conn, Map<String, String> schemaMap, String schema) throws SQLException
    {

        String sql = com.pivotal.pcf.mysqlweb.dao.tables.Constants.USER_TABLES_COUNT +
                "union " +
                com.pivotal.pcf.mysqlweb.dao.views.Constants.USER_VIEWS_COUNT +
                "union " +
                com.pivotal.pcf.mysqlweb.dao.indexes.Constants.USER_INDEXES_COUNT +
                "union " +
                com.pivotal.pcf.mysqlweb.dao.constraints.Constants.USER_CONSTRAINTS_COUNT;

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        Map<String, String> schemaMapLocal = schemaMap;

        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, schema);
            pstmt.setString(2, schema);
            pstmt.setString(3, schema);
            pstmt.setString(4, schema);

            rset = pstmt.executeQuery();
            while (rset.next())
            {
                schemaMapLocal.put(rset.getString(1).trim(), rset.getString(2));
            }

            logger.info("schemaMapLocal = " + schemaMapLocal);
        }
        finally
        {
            JDBCUtil.close(pstmt);
        }

        return schemaMapLocal;
    }
}
