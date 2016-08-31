package com.pivotal.pcf.mysqlweb.dao.constraints;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface ConstraintDAO
{
    public List<Constraint> retrieveConstraintList(String schema, String search, String userKey) throws PivotalMySQLWebException;

    public Result simpleconstraintCommand (String schemaName, String constraintName, String type, String userKey) throws PivotalMySQLWebException;

}
