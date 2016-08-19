package com.pivotal.pcf.mysqlweb.dao.indexes;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface IndexDAO
{
    public List<Index> retrieveIndexList(String schema, String search, String userKey) throws PivotalMySQLWebException;

    public Result simpleindexCommand (String schemaName, String indexName, String type, String userKey) throws PivotalMySQLWebException;
}
