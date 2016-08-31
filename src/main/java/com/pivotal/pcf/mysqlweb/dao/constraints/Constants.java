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
