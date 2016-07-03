package com.pivotal.pcf.mysqlweb.utils;

import java.sql.Connection;

public class MysqlConnection
{
    private Connection conn;
    private String url;
    private String connectedAt;
    private String schema;

    public MysqlConnection
            (Connection conn, String url, String connectedAt, String schema)
    {
        super();
        this.conn = conn;
        this.url = url;
        this.connectedAt = connectedAt;
        this.schema = schema;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(String connectedAt) {
        this.connectedAt = connectedAt;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
