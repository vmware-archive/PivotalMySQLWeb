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
package com.pivotal.pcf.mysqlweb.dao.generic;

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;
import java.util.Map;

public interface GenericDAO
{
    WebResult runGenericQuery (String sql, Object[] args, String userKey, int maxRows) throws PivotalMySQLWebException;

    CommandResult runStatement(String sql, String elapsedTime, String ddl, String userKey) throws PivotalMySQLWebException;

    Map<String, Long> populateSchemaMap(String schema, String userKey) throws PivotalMySQLWebException;

    List<String> allSchemas (String userKey) throws PivotalMySQLWebException;

    Result runCommand (String sql, String userKey);
}
