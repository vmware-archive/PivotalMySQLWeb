package com.pivotal.pcf.mysqlweb.dao.views;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface ViewDAO
{
    public List<View> retrieveViewList(String schema, String search, String userKey) throws PivotalMySQLWebException;

    public Result simpleviewCommand (String schemaName, String viewName, String type, String userKey) throws PivotalMySQLWebException;

    public String getViewDefinition(String schemaName, String viewName, String userKey) throws PivotalMySQLWebException;
}
