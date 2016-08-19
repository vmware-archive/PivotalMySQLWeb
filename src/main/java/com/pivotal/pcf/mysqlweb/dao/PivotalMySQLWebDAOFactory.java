package com.pivotal.pcf.mysqlweb.dao;

import com.pivotal.pcf.mysqlweb.dao.indexes.IndexDAO;
import com.pivotal.pcf.mysqlweb.dao.indexes.IndexDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.tables.TableDAO;
import com.pivotal.pcf.mysqlweb.dao.tables.TableDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.views.ViewDAO;
import com.pivotal.pcf.mysqlweb.dao.views.ViewDAOImpl;

public class PivotalMySQLWebDAOFactory
{
    public static TableDAO getTableDAO()
    {
        return new TableDAOImpl();
    }

    public static ViewDAO getViewDAO()
    {
        return new ViewDAOImpl();
    }

    public static IndexDAO getIndexDAO()
    {
        return new IndexDAOImpl();
    }
}
