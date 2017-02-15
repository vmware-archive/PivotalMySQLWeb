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
