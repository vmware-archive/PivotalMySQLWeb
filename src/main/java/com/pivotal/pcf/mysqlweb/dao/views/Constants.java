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
