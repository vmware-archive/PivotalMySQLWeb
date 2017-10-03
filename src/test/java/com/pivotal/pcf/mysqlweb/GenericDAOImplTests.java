package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GenericDAOImplTests extends PivotalMySqlWebApplicationTests
{

    private static SingleConnectionDataSource dataSource;
    private static ConnectionManager cm;

    @Autowired
    private static GenericDAOImpl genericDAO;

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
        genericDAO.setDataSource(dataSource);

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);
    }

    @Test
    public void t1TestCreateTable () throws Exception
    {
        CommandResult commandResult;

        commandResult =
                genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", userKey);
        Assert.assertEquals(commandResult.getMessage(), "SUCCESS");

        commandResult =
                    genericDAO.runStatement("insert into pas_yyy values (1)", "N", "Y", userKey);
        Assert.assertEquals(commandResult.getMessage(), "SUCCESS");
    }

    @Test
    public void t2TestQueryTable () throws Exception
    {
        WebResult webResult =
                genericDAO.runGenericQuery("select * from pas_yyy", null, userKey, -1);

        Assert.assertEquals(webResult.getRows().size(), 1);
        Assert.assertEquals(webResult.getColumnNames().length, 1);
    }

}
