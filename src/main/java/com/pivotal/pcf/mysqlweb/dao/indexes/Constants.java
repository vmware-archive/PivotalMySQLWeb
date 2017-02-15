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

public interface Constants
{
    public static final String USER_INDEXES =
            " select\n" +
                    "        table_catalog as \"Catalog\",\n " +
                    "        table_schema as \"Schema\",\n " +
                    "        table_name as \"Name\",\n " +
                    "        index_name as \"Index Name\"\n " +
                    "        from INFORMATION_SCHEMA.STATISTICS\n " +
                    "        where table_schema = ? " +
                    "        and index_name like ? " +
                    "  ORDER BY 2,3 ";

    public static final String USER_INDEXES_COUNT =
            "select object_type, count(*) " +
                    "from ( " +
                    "  SELECT table_schema as \"Schema\", " +
                    "    table_name as \"Name\", " +
                    "    'Index' as object_type " +
                    "  from INFORMATION_SCHEMA.STATISTICS\n " +
                    "  where table_schema = ? " +
                    "  ORDER BY 1,2) a " +
                    "group by object_type ";

    public static final String INDEX_DETAILS =
                    " select *\n " +
                    "        from INFORMATION_SCHEMA.STATISTICS\n " +
                    "        where table_schema = ? " +
                    "        and table_name = ? " +
                    "        and index_name = ? ";

    public static String DROP_INDEX_PRIMARY = "alter table %s.%s drop primary key";

    public static String DROP_INDEX = "alter table %s.%s drop index %s";

}

