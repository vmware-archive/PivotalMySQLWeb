package com.pivotal.pcf.mysqlweb.dao.indexes;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface IndexDAO
{
    public List<Index> retrieveIndexList(String schema, String search, String userKey) throws PivotalMySQLWebException;

    public Result simpleindexCommand (String schemaName, String indexName, String type, String tableName, String userKey) throws PivotalMySQLWebException;

    public javax.servlet.jsp.jstl.sql.Result getIndexDetails(String schema, String tableName, String indexName, String userKey) throws PivotalMySQLWebException;

}
