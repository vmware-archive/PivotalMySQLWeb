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
