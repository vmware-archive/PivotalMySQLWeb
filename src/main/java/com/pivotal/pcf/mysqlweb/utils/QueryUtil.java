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
package com.pivotal.pcf.mysqlweb.utils;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryUtil
{
    protected static Logger logger = Logger.getLogger(QueryUtil.class);

    static public String runQueryForCSV (Connection conn, String query) throws SQLException, IOException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        StringWriter sw = new StringWriter();

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);

            CSVWriter csvWriter = new CSVWriter(sw, ',', '"', '\"');
            csvWriter.writeAll(rset, true);
            csvWriter.flush();

        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return sw.toString();
    }

    static public String runQueryForJSON (Connection conn, String query) throws SQLException, IOException
    {
        Statement stmt  = null;
        ResultSet rset  = null;
        String jsonResult = null;

        try
        {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);

            jsonResult = DSL.using(conn).fetch(rset).formatJSON();

        }
        finally
        {
            JDBCUtil.close(stmt);
            JDBCUtil.close(rset);
        }

        return jsonResult;
    }

}
