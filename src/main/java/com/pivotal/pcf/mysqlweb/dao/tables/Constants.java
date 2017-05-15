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
package com.pivotal.pcf.mysqlweb.dao.tables;

public interface Constants
{

        public static final String USER_TABLES =
                        " select\n" +
                        "        table_catalog as \"Catalog\",\n " +
                        "        table_schema as \"Schema\",\n " +
                        "        table_name as \"Name\",\n " +
                        "        table_type as \"Type\" " +
                        "        from information_schema.tables\n " +
                        "        where table_schema = ? " +
                        "        and table_name like ? " +
                        "  ORDER BY 2,3 ";

        public static final String TABLE_DETAILS =
                        " select * \n" +
                        "        from information_schema.tables\n " +
                        "        where table_schema = ? " +
                        "        and table_name = ? ";

        public static final String USER_TABLES_COUNT =
            "select object_type, count(*) " +
                    "from ( " +
                    "  SELECT table_schema as \"Schema\", " +
                    "    table_name as \"Name\", " +
                    "    'Table' as object_type " +
                    "  from information_schema.tables\n " +
                    "  where table_schema = ? " +
                    "  ORDER BY 1,2) a " +
                    "group by object_type ";

        public static String DROP_TABLE = "DROP TABLE %s.%s";

        public static String TRUNCATE_TABLE = "TRUNCATE TABLE %s.%s";

        public static String DROP_TABLE_PUBLIC = "DROP TABLE %s";

        public static String TRUNCATE_TABLE_PUBLIC = "TRUNCATE TABLE %s";

        public static String CREATE_TABLE_QUERY = "SHOW CREATE TABLE %s.%s";

        public static final String TABLE_STRUCTURE =
                        "describe %s.%s";

        public static String SHOW_INDEXES = "SHOW INDEX FROM %s.%s";
}
