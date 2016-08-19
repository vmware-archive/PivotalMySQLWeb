package com.pivotal.pcf.mysqlweb.dao.indexes;

public class Index
{
    public String catalog;
    public String schemaName;
    public String tableName;
    public String indexName;

    public Index()
    {
    }

    public Index(String catalog, String schemaName, String tableName, String indexName) {
        this.catalog = catalog;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.indexName = indexName;
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

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public String toString() {
        return "Index{" +
                "catalog='" + catalog + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", indexName='" + indexName + '\'' +
                '}';
    }
}
