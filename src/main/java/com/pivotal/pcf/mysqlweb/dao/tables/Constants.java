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

        public static final String USER_TAB_COLUMNS =
                        "SELECT COLUMN_NAME, data_type, is_nullable " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = ? " +
                        "AND TABLE_NAME = ? " +
                        "order by ORDINAL_POSITION";

        public static String SHOW_INDEXES = "SHOW INDEX FROM %s.%s";
}
