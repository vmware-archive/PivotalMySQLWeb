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

    public static String DROP_INDEX = "DROP INDEX %s.%s";

}
