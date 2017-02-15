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
