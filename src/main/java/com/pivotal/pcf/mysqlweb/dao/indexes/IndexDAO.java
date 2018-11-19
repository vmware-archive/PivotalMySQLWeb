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
package com.pivotal.pcf.mysqlweb.dao.indexes;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;

import java.util.List;

public interface IndexDAO
{
    public void setDataSource(javax.sql.DataSource ds);
    public List<Index> retrieveIndexList(String schema, String search, String userKey) throws PivotalMySQLWebException;
    public Result simpleindexCommand (String schemaName, String indexName, String type, String tableName, String userKey) throws PivotalMySQLWebException;
    public WebResult getIndexDetails(String schema, String tableName, String indexName, String userKey) throws PivotalMySQLWebException;

}
