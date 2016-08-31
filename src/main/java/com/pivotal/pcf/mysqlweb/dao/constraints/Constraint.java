package com.pivotal.pcf.mysqlweb.dao.constraints;

public class Constraint
{
    public String catalog;
    public String schemaName;
    public String constraintName;
    public String tableName;
    public String constraintType;

    public Constraint()
    {
    }

    public Constraint(String catalog, String schemaName, String constraintName, String tableName, String constraintType) {
        this.catalog = catalog;
        this.schemaName = schemaName;
        this.constraintName = constraintName;
        this.tableName = tableName;
        this.constraintType = constraintType;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "catalog='" + catalog + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", constraintName='" + constraintName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", constraintType='" + constraintType + '\'' +
                '}';
    }
}
