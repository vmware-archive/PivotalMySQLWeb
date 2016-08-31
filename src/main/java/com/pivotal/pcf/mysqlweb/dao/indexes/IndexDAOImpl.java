package com.pivotal.pcf.mysqlweb.dao.indexes;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.JDBCUtil;
import org.apache.log4j.Logger;

import javax.servlet.jsp.jstl.sql.ResultSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IndexDAOImpl implements IndexDAO
{
    protected static Logger logger = Logger.getLogger("controller");

    @Override
    public List<Index> retrieveIndexList(String schema, String search, String userKey) throws PivotalMySQLWebException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        List<Index>       indexes = null;
        String            srch = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);

            stmt = conn.prepareStatement(Constants.USER_INDEXES);

            if (search == null)
                srch = "%";
            else
                srch = "%" + search + "%";


            stmt.setString(1, schema);
            stmt.setString(2, srch);
            rset = stmt.executeQuery();

            indexes = makeIndexListFromResultSet(rset);
        }
        catch (SQLException se)
        {
            logger.info("Error retrieving all indexes with search string = " + search);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex)
        {
            logger.info("Error retrieving all indexes with search string = " + search);
            throw new PivotalMySQLWebException(ex);
        }
        finally
        {
            // close all resources
            JDBCUtil.close(rset);
            JDBCUtil.close(stmt);
        }

        return indexes;
    }

    @Override
    public Result simpleindexCommand(String schemaName, String indexName, String type, String tableName, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = null;

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

        res = PivotalMySQLWebDAOUtil.runCommand(command, userKey);

        return res;
    }

    @Override
    public javax.servlet.jsp.jstl.sql.Result getIndexDetails(String schema, String tableName, String indexName, String userKey) throws PivotalMySQLWebException
    {
        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rset = null;
        javax.servlet.jsp.jstl.sql.Result res = null;

        try
        {
            conn = AdminUtil.getConnection(userKey);
            stmt = conn.prepareStatement(Constants.INDEX_DETAILS);
            stmt.setString(1, schema);
            stmt.setString(2, tableName);
            stmt.setString(3, indexName);
            rset = stmt.executeQuery();

            res = ResultSupport.toResult(rset);

        }
        catch (SQLException se)
        {
            logger.info("Error retrieving index details for index " + indexName);
            throw new PivotalMySQLWebException(se);
        }
        catch (Exception ex) {
            logger.info("Error retrieving index details for index " + indexName);
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

    private List<Index> makeIndexListFromResultSet (ResultSet rset) throws SQLException
    {
        List<Index> indexes = new ArrayList<Index>();

        while (rset.next())
        {
            Index index = new Index(rset.getString(1),
                    rset.getString(2),
                    rset.getString(3),
                    rset.getString(4));

            indexes.add(index);
        }

        return indexes;

    }
}
