/*
PivotalMySQLWeb

Copyright (c) 2017-Present Pivotal Software, Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.pivotal.pcf.mysqlweb.dao;

import com.pivotal.pcf.mysqlweb.dao.constraints.ConstraintDAO;
import com.pivotal.pcf.mysqlweb.dao.constraints.ConstraintDAOImpl;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAOImpl;
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

    public static ConstraintDAO getConstraintDAO()
    {
        return new ConstraintDAOImpl();
    }

    public static GenericDAO getGenericDAO()
    {
        return new GenericDAOImpl();
    }
}
