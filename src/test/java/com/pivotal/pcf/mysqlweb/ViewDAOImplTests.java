package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.tables.Table;
import com.pivotal.pcf.mysqlweb.dao.views.View;
import com.pivotal.pcf.mysqlweb.dao.views.ViewDAOImpl;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ViewDAOImplTests extends PivotalMySqlWebApplicationTests {

    private static SingleConnectionDataSource dataSource;
    private static ConnectionManager cm;

    @Autowired
    private static GenericDAOImpl genericDAO;

    @Autowired
    private static ViewDAOImpl viewDAO;

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
        viewDAO = new ViewDAOImpl();
        genericDAO.setDataSource(dataSource);

        genericDAO.runStatement("create table pas_yyy (col1 int)", "N", "Y", userKey);
        genericDAO.runStatement("insert into pas_yyy (1)", "N", "N", userKey);
        genericDAO.runStatement("create view pas_yyy_view as select * from pas_yyy", "N", "Y", userKey);

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);
    }

    @Test
    public void t1retrieveTableList () throws Exception
    {
        List<View> views = viewDAO.retrieveViewList(database, null, userKey);

        Assert.assertTrue(views.size() >= 1);
    }

    @Test
    public void t2getViewDefinition () throws Exception
    {
        String result = null;

        result = viewDAO.getViewDefinition(database, "PAS_YYY_VIEW", userKey);

        Assert.assertNotNull(result);
    }

    @Test
    public void t3simpleviewCommand () throws Exception
    {
        Result result = viewDAO.simpleviewCommand(database, "PAS_YYY_VIEW", "DROP", userKey);

        Assert.assertEquals(result.getMessage(), "SUCCESS");
    }
}
