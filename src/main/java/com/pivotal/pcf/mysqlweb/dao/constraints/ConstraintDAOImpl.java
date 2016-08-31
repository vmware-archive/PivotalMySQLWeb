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
    protected static Logger logger = Logger.getLogger("controller");

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
    public Result simpleconstraintCommand(String schemaName, String constraintName, String type, String userKey) throws PivotalMySQLWebException
    {
        String            command = null;
        Result            res     = null;

        if (type != null)
        {
            if (type.equalsIgnoreCase("DROP"))
            {
                command = String.format(Constants.DROP_CONSTRAINT_FK, schemaName, constraintName);
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
