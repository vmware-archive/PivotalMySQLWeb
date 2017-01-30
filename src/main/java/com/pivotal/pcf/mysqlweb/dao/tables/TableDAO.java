package com.pivotal.pcf.mysqlweb.dao.tables;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface TableDAO
{
    public List<Table> retrieveTableList(String schema, String search, String userKey) throws PivotalMySQLWebException;

    public Result simpletableCommand (String schemaName, String tableName, String type, String userKey) throws PivotalMySQLWebException;

    public javax.servlet.jsp.jstl.sql.Result getTableStructure (String schema, String tableName, String userKey) throws PivotalMySQLWebException;

    public javax.servlet.jsp.jstl.sql.Result getTableDetails (String schema, String tableName, String userKey) throws PivotalMySQLWebException;

    public String runShowQuery (String schema, String tableName, String userKey) throws PivotalMySQLWebException;

    public javax.servlet.jsp.jstl.sql.Result showIndexes(String schema, String tableName, String userKey) throws PivotalMySQLWebException;
}
