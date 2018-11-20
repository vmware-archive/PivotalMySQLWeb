package com.pivotal.pcf.mysqlweb;

import org.testcontainers.containers.MySQLContainer;

public class MySQLContainer57 extends MySQLContainer {

    public MySQLContainer57() {
        super();
    }

    public MySQLContainer57(String dockerImageName) {
        super(dockerImageName);
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}