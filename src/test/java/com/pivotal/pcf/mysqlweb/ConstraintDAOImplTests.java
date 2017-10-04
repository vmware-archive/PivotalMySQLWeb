package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.dao.constraints.Constraint;
import com.pivotal.pcf.mysqlweb.dao.constraints.ConstraintDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.views.View;
import com.pivotal.pcf.mysqlweb.dao.views.ViewDAOImpl;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConstraintDAOImplTests extends PivotalMySqlWebApplicationTests {
    private static SingleConnectionDataSource dataSource;
    private static ConnectionManager cm;

    @Autowired
    private static GenericDAOImpl genericDAO;

    @Autowired
    private static ConstraintDAOImpl constraintDAO;

    @BeforeClass
    public static void setUp() throws Exception
    {
        cm = ConnectionManager.getInstance();

        dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        cm.addDataSourceConnection(dataSource, userKey);
        genericDAO = new GenericDAOImpl();
        constraintDAO = new ConstraintDAOImpl();
        genericDAO.setDataSource(dataSource);

        genericDAO.runStatement("create table pas_yyy (col1 int NOT NULL PRIMARY KEY)", "N", "Y", userKey);

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);
    }

    @Test
    public void t1retrieveConstraintList () throws Exception
    {
        List<Constraint> constraints = constraintDAO.retrieveConstraintList(database, null, userKey);

        Assert.assertTrue(constraints.size() >= 1);
    }
}
