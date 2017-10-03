package com.pivotal.pcf.mysqlweb;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PivotalMySqlWebApplication.class)
public abstract class PivotalMySqlWebApplicationTests
{
    static String userKey = "apples-key";
    static String url = "jdbc:mysql://localhost:3306/apples";
    static String username = "pas";
    static String password = "pas";
    static String database = "apples";
}
