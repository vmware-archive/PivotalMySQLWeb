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

public interface Constants
{
    String DATABASE_LIST =
            "SELECT SCHEMA_NAME 'database', default_character_set_name 'charset', DEFAULT_COLLATION_NAME 'collation' FROM information_schema.SCHEMATA";

    String SCHEMA_MAP_QUERY = com.pivotal.pcf.mysqlweb.dao.tables.Constants.USER_TABLES_COUNT +
            "union " +
            com.pivotal.pcf.mysqlweb.dao.views.Constants.USER_VIEWS_COUNT +
            "union " +
            com.pivotal.pcf.mysqlweb.dao.indexes.Constants.USER_INDEXES_COUNT +
            "union " +
            com.pivotal.pcf.mysqlweb.dao.constraints.Constants.USER_CONSTRAINTS_COUNT;

    String ALL_SCHEMAS = "select schema_name from information_schema.schemata order by 1";

}
