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

public class View
{
    public String catalog;
    public String schemaName;
    public String viewName;

    public View()
    {
    }

    public View(String catalog, String schemaName, String viewName) {
        this.catalog = catalog;
        this.schemaName = schemaName;
        this.viewName = viewName;
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

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return "View{" +
                "catalog='" + catalog + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}
