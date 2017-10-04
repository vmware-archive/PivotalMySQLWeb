package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.indexes.Index;
import com.pivotal.pcf.mysqlweb.dao.indexes.IndexDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.tables.Table;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexDAOImplTests extends PivotalMySqlWebApplicationTests {
    private static SingleConnectionDataSource dataSource;
    private static ConnectionManager cm;

    @Autowired
    private static GenericDAOImpl genericDAO;

    @Autowired
    private static IndexDAOImpl indexDAO;

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
        indexDAO = new IndexDAOImpl();
        genericDAO.setDataSource(dataSource);

        genericDAO.runStatement("create table pas_yyy (col1 int NOT NULL, CONSTRAINT PK_pas_yyy PRIMARY KEY (COL1))", "N", "Y", userKey);

    }

    @AfterClass
    public static void cleanUp() throws Exception {
        genericDAO.runStatement("drop table pas_yyy", "N", "Y", userKey);
    }

    @Test
    public void t1retrieveIndexList () throws Exception
    {
        List<Index> indexes = indexDAO.retrieveIndexList(database, null, userKey);

        Assert.assertTrue(indexes.size() >= 1);
    }

    @Test
    public void t2getIndexDetails () throws Exception
    {
        WebResult webResult = indexDAO.getIndexDetails(database, "PAS_YYY", "PRIMARY", userKey);

        Assert.assertEquals(webResult.getRows().size(), 1);
    }

    @Test
    public void t3simpleindexCommand () throws Exception
    {
        Result result = indexDAO.simpleindexCommand(database, "PRIMARY", "DROP", "PAS_YYY", userKey);

        Assert.assertEquals(result.getMessage(), "SUCCESS");
    }
}
