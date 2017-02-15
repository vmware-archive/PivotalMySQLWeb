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
package com.pivotal.pcf.mysqlweb.dao.constraints;

public interface Constants
{
    public static final String USER_CONSTRAINTS =
            "select CONSTRAINT_CATALOG, " +
                    "CONSTRAINT_SCHEMA, " +
                    "CONSTRAINT_NAME, " +
                    "TABLE_NAME, " +
                    "CONSTRAINT_TYPE " +
                    "from information_schema.TABLE_CONSTRAINTS " +
                    "where CONSTRAINT_SCHEMA = ? " +
                    "and CONSTRAINT_NAME like ? " +
                    "order by 4, 3 ";

    public static final String USER_CONSTRAINTS_COUNT =
            "select object_type, count(*) " +
                    "from ( " +
                    "  SELECT CONSTRAINT_SCHEMA as \"Schema\", " +
                    "    table_name as \"Name\", " +
                    "    'Constraint' as object_type " +
                    "  from information_schema.TABLE_CONSTRAINTS\n " +
                    "  where CONSTRAINT_SCHEMA = ? " +
                    "  ORDER BY 1,2) a " +
                    "group by object_type ";

    public static String DROP_CONSTRAINT_FK = "ALTER TABLE %s.%s DROP FOREIGN KEY %s";

    public static String DROP_CONSTRAINT_UNIQUE = "ALTER TABLE %s.%s DROP INDEX %s";

    public static String DROP_CONSTRAINT_PRIMARY_KEY = "ALTER TABLE %s.%s drop primary key";

}
