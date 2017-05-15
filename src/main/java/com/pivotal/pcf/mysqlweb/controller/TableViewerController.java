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
package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.tables.Constants;
import com.pivotal.pcf.mysqlweb.dao.tables.TableDAO;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;

@Controller
public class TableViewerController
{
    protected static Logger logger = Logger.getLogger(TableViewerController.class);

    private String tableRows = "select * from %s limit 30";

    @RequestMapping(value = "/tableviewer", method = RequestMethod.GET)
    public String showTables
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        logger.info("Received request to show table viewer page");

        String schema = null;
        javax.servlet.jsp.jstl.sql.Result describeStructure, tableData, queryResultsDescribe, tableDetails, tableIndexes;

        TableDAO tableDAO = PivotalMySQLWebDAOFactory.getTableDAO();

        String selectedSchema = request.getParameter("selectedSchema");
        String tabName = (String)request.getParameter("tabName");

        logger.info("selectedSchema = " + selectedSchema);
        logger.info("tabName = " + tabName);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String) session.getAttribute("schema");
        }

        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = cm.getConnection(session.getId());

        // describe table
        String ddl = tableDAO.runShowQuery(schema,
                tabName,
                (String)session.getAttribute("user_key"));

        model.addAttribute("tableDDL", ddl.trim());
        model.addAttribute("tablename", tabName.toUpperCase());

        // get table rows
        tableData = QueryUtil.runQuery(conn, String.format(tableRows, tabName), -1);
        model.addAttribute("queryResults", tableData);

        // describe table
        queryResultsDescribe = QueryUtil.runQuery(conn,
                                        String.format(Constants.TABLE_STRUCTURE, schema, tabName),
                                       -1);
        model.addAttribute("queryResultsDescribe", queryResultsDescribe);

        // view all table details
        tableDetails =
                tableDAO.getTableDetails
                        (schema, (String)request.getParameter("tabName"), (String)session.getAttribute("user_key"));


        model.addAttribute("tableDetails", tableDetails);

        // view table indexes
        tableIndexes = tableDAO.showIndexes(schema,
                tabName,
                (String)session.getAttribute("user_key"));

        model.addAttribute("tableIndexes", tableIndexes);

        return "tableviewer";
    }
}
