package com.pivotal.pcf.mysqlweb.dao.views;

import java.util.List;

import javax.annotation.PostConstruct;

import com.pivotal.pcf.mysqlweb.MySQLContainer57;
import com.pivotal.pcf.mysqlweb.PivotalMySqlWebTestApplication;
import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    initializers = { ViewDAOImplTests.TestContainerInitializer.class })
public class ViewDAOImplTests {

    private static MySQLContainer57 mysqlContainer = 
        (MySQLContainer57) new MySQLContainer57()
            .withStartupTimeoutSeconds(450);

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private ViewDAO viewDAO;

    @BeforeAll
    public static void startup() {
        mysqlContainer.start();
    }

    @AfterAll
    public static void shutdown() {
        mysqlContainer.stop();
    }

    @BeforeEach
    public void setUp() throws Exception {
        genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", "ds-key");
        genericDAO.runStatement("insert into pas_yyy(col1) values (1)", "N", "N", "ds-key");
        genericDAO.runStatement("create view pas_yyy_view as select * from pas_yyy", "N", "Y", "ds-key");
    }

    @AfterEach
    public void cleanUp() throws Exception {
        genericDAO.runStatement("drop view pas_yyy_view", "N", "N", "ds-key");
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", "ds-key");
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
    public void t1retrieveViewList () throws Exception
    {
        List<View> views = viewDAO.retrieveViewList(mysqlContainer.getDatabaseName(), null, "ds-key");
        Assertions.assertTrue(views.size() >= 1);
    }

    @Test
    public void t2getViewDefinition () throws Exception
    {
        String result = viewDAO.getViewDefinition(mysqlContainer.getDatabaseName(), "pas_yyy_view", "ds-key");
        Assertions.assertNotNull(result);
    }

    @Test
    public void t3simpleviewCommand () throws Exception
    {
        Result result = viewDAO.simpleviewCommand(mysqlContainer.getDatabaseName(), "pas_yyy_view", "drop", "ds-key");
        Assertions.assertEquals(result.getMessage(), "SUCCESS");
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
