package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

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
        dataSource.setUrl("jdbc:mysql://localhost:3306/apples");
        dataSource.setUsername("pas");
        dataSource.setPassword("pas");

        cm.addDataSourceConnection(dataSource, "apples-key");
        genericDAO = new GenericDAOImpl();
        genericDAO.setDataSource(dataSource);

    }

    @Test
    public void test1 () throws Exception
    {
        CommandResult commandResult;

        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);

        commandResult =
                genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", userKey);

        Assert.assertEquals(commandResult.getMessage(), "SUCCESS");
    }

    @Test
    public void test2 () throws Exception
    {
        CommandResult commandResult =
                genericDAO.runStatement("insert into pas_yyy values (1)", "N", "Y", userKey);

        Assert.assertEquals(commandResult.getMessage(), "SUCCESS");
    }

}
