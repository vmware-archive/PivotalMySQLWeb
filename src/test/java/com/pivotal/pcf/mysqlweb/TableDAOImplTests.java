package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.tables.Table;
import com.pivotal.pcf.mysqlweb.dao.tables.TableDAOImpl;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TableDAOImplTests extends PivotalMySqlWebApplicationTests {

    private static SingleConnectionDataSource dataSource;
    private static ConnectionManager cm;

    @Autowired
    private static GenericDAOImpl genericDAO;

    @Autowired
    private static TableDAOImpl tableDAO;

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
        tableDAO = new TableDAOImpl();
        genericDAO.setDataSource(dataSource);

        genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", userKey);
        genericDAO.runStatement("insert into pas_yyy (1)", "N", "N", userKey);

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);
    }

    @Test
    public void t1retrieveTableList () throws Exception
    {
        List<Table> tbls = tableDAO.retrieveTableList(database, null, userKey);

        Assert.assertTrue(tbls.size() >= 1);
    }

    @Test
    public void t2simpletableCommand () throws Exception
    {
        Result result = tableDAO.simpletableCommand(database, "PAS_YYY", "EMPTY", userKey);

        Assert.assertEquals(result.getMessage(), "SUCCESS");

    }

    @Test
    public void t3getTableStructure () throws Exception
    {
        WebResult webResult = tableDAO.getTableStructure(database, "PAS_YYY", userKey);

        Assert.assertEquals(webResult.getRows().size(), 1);
    }

    @Test
    public void t4getTableDetails () throws Exception
    {
        WebResult webResult = tableDAO.getTableDetails(database, "PAS_YYY", userKey);

        Assert.assertEquals(webResult.getRows().size(), 1);
    }

    @Test
    public void t5runShowQuery () throws Exception
    {
        String result = null;

        result = tableDAO.runShowQuery(database, "PAS_YYY", userKey);

        Assert.assertNotNull(result);
    }

}
