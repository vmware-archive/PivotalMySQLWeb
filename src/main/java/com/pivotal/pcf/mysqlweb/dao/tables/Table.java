package com.pivotal.pcf.mysqlweb.dao.tables;

public class Table
{
    public String catalog;
    public String schemaName;
    public String tableName;
    public String owner;

    public Table()
    {
    }

    public Table(String catalog, String schemaName, String tableName, String owner) {
        this.catalog = catalog;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.owner = owner;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Table{" +
                "catalog='" + catalog + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
