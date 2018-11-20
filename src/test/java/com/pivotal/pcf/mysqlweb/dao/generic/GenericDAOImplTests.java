package com.pivotal.pcf.mysqlweb.dao.generic;

import javax.annotation.PostConstruct;

import com.pivotal.pcf.mysqlweb.MySQLContainer57;
import com.pivotal.pcf.mysqlweb.PivotalMySqlWebTestApplication;
import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PivotalMySqlWebTestApplication.class })
@ContextConfiguration(
    initializers = { GenericDAOImplTests.TestContainerInitializer.class })
public class GenericDAOImplTests {

    private static MySQLContainer57 mysqlContainer = 
        (MySQLContainer57) new MySQLContainer57()
            .withStartupTimeoutSeconds(450);

    @Autowired
    private GenericDAO genericDAO;

    @BeforeAll
    public static void startup() {
        mysqlContainer.start();
    }

    @AfterAll
    public static void shutdown() {
        mysqlContainer.stop();
    }

    @PostConstruct
    public void init() throws Exception {
        ConnectionManager
            .getInstance()
                .addDataSourceConnection(
                    new SingleConnectionDataSource(
                        mysqlContainer.getJdbcUrl(), 
                        mysqlContainer.getUsername(), 
                        mysqlContainer.getPassword(), 
                        false), 
                        "ds-key");
    }

    @Test
    public void testCreateAndQueryTable() throws Exception {
        CommandResult commandResult =
                genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", "ds-key");
        Assertions.assertEquals(commandResult.getMessage(), "SUCCESS");

        commandResult =
                    genericDAO.runStatement("insert into pas_yyy(col1) values (1)", "N", "Y", "ds-key");
        Assertions.assertEquals(commandResult.getMessage(), "SUCCESS");

        WebResult webResult =
                genericDAO.runGenericQuery("select * from pas_yyy", null, "ds-key", -1);

        Assertions.assertEquals(webResult.getRows().size(), 1);
        Assertions.assertEquals(webResult.getColumnNames().length, 1);
    }

    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues
                .of(
                    "spring.datasource.url=" + mysqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mysqlContainer.getUsername(),
                    "spring.datasource.password=" + mysqlContainer.getPassword(),
                    "spring.datasource.driver-class-name=" + mysqlContainer.getDriverClassName()
                )
                .applyTo(applicationContext.getEnvironment());
        }
    }

}
