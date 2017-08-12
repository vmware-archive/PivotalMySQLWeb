package com.pivotal.pcf.mysqlweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PivotalMySqlWebApplication.class)
public abstract class PivotalMySqlWebApplicationTests
{
    public static String userKey = "apples-key";
}
