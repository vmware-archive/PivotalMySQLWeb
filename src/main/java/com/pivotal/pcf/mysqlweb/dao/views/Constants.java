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
package com.pivotal.pcf.mysqlweb.dao.views;

public interface Constants
{
        public static final String USER_VIEWS =
              "select table_catalog, " +
              "       table_schema, " +
              "       table_name " +
              "from information_schema.views " +
              "where table_schema = ? " +
              "and table_name like ? " +
              "order by 2, 3";


            public static final String USER_VIEWS_COUNT =
                    "select object_type, count(*) " +
                            "from ( " +
                            "select table_catalog, " +
                            "       table_schema, " +
                            "       table_name, " +
                            "       'View' as object_type " +
                            "from information_schema.views " +
                            "where table_schema = ? " +
                            "  ORDER BY 1,2) a " +
                            "group by object_type ";

        public static final String USER_VIEW_DEF =
                "select view_definition " +
                "from information_schema.views " +
                "where table_schema = ? " +
                "and   table_name = ? ";

        public static String DROP_VIEW = "drop view %s.%s";

        public static String DROP_VIEW_PUBLIC = "drop view %s";
}
