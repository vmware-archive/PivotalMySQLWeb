package com.pivotal.pcf.mysqlweb.beans;

public class Login
{
    private String username;
    private String password;
    private String url;
    private String schema;

    public Login()
    {
    }

    public Login(String username, String password, String url, String schema)
    {
        this.username = username;
        this.password = password;
        this.url = url;
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
