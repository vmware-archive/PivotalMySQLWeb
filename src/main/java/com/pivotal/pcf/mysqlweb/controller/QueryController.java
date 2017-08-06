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

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.dao.tables.Constants;
import com.pivotal.pcf.mysqlweb.main.PivotalMySQLWebException;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.sql.Result;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class QueryController
{
    protected static Logger logger = Logger.getLogger(QueryController.class);
    private static final String FILENAME = "worksheet.sql";
    private static final String FILENAME_EXPORT = "query-output.csv";
    private static final String FILENAME_EXPORT_JSON = "query-output.json";
    private static final String SAVE_CONTENT_TYPE = "application/x-download";
    private final String QUERY_TYPES[] = {
            "SELECT", "INSERT", "DELETE", "DDL", "UPDATE", "CALL", "COMMIT", "ROLLBACK", "DESCRIBE"
    };

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String worksheet
            (Model model, HttpServletResponse response,
             HttpServletRequest request,
             HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        logger.info("Received request to show query worksheet");
        UserPref userPrefs = (UserPref) session.getAttribute("prefs");
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        String action = request.getParameter("action");
        if (action != null)
        {
            CommandResult result = new CommandResult();
            ConnectionManager cm = ConnectionManager.getInstance();
            Connection conn = cm.getDataSource(session.getId()).getConnection();

            if (action.trim().equals("commit"))
            {
                logger.info("commit action requested");
                result = genericDAO.runStatement("commit", "N", "Y", (String)session.getAttribute("user_key"));
                addCommandToHistory(session, userPrefs, "commit");
                model.addAttribute("result", result);
            }
            else if (action.trim().equals("rollback"))
            {
                logger.info("rollback action requested");
                result = genericDAO.runStatement("rollback", "N", "Y", (String)session.getAttribute("user_key"));
                addCommandToHistory(session, userPrefs, "rollback");
                model.addAttribute("result", result);
            }
            else if (action.trim().equals("export"))
            {
                logger.info("export data to CSV action requested");
                String query = request.getParameter("query");
                String exportDataCSV = QueryUtil.runQueryForCSV(conn, query);

                response.setContentType(SAVE_CONTENT_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" + FILENAME_EXPORT);

                ServletOutputStream out = response.getOutputStream();
                out.println(exportDataCSV);
                out.close();
                return null;
            }
            else if (action.trim().equals("export_json"))
            {
                logger.info("export data to JSON action requested");
                String query = request.getParameter("query");
                String exportDataJSON = QueryUtil.runQueryForJSON(conn, query);

                response.setContentType(SAVE_CONTENT_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" + FILENAME_EXPORT_JSON);

                ServletOutputStream out = response.getOutputStream();
                out.println(exportDataJSON);
                out.close();
                return null;
            }

        }

        model.addAttribute("query", "");
        model.addAttribute("queryCount", "N");
        model.addAttribute("elapsedTime", "N");
        model.addAttribute("explainPlan", "N");

        return "query";
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String worksheetAction
            (Model model,
             @RequestParam(value="query", required=true) String query,
             @RequestParam(value="queryCount", required=true) String queryCount,
             @RequestParam(value="elapsedTime", required=true) String elapsedTime,
             @RequestParam(value="explainPlan", required=true) String explainPlan,
             HttpServletResponse response,
             HttpServletRequest request,
             HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        logger.info("Received request to action SQL from query worksheet");
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = cm.getDataSource(session.getId()).getConnection();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        UserPref userPrefs = (UserPref) session.getAttribute("prefs");

        logger.info("Query = [" + query + "]");
        String [] splitQueryStr = spiltQuery(query);

        CommandResult result = new CommandResult();
        WebResult webResult;

        if (query.trim().length() != 0)
        {
            if (splitQueryStr.length == 1)
            {
                String s = checkForComments(query);
                s = s.trim();

                if (determineQueryType(s).equals("SELECT")) {
                    try {
                        if (explainPlan.equals("Y"))
                        {
                            logger.info("Need to run explain plan");

                            webResult = genericDAO.runGenericQuery
                                    ("explain " + s, null, (String)session.getAttribute("user_key"), -1);
                            model.addAttribute("explainresult", webResult);
                            model.addAttribute("query", s);
                        }
                        else
                        {
                            long start = System.currentTimeMillis();
                            webResult = genericDAO.runGenericQuery
                                    (s, null, (String)session.getAttribute("user_key"), userPrefs.getMaxRecordsinSQLQueryWindow());
                            long end = System.currentTimeMillis();

                            double timeTaken = new Double(end - start).doubleValue();
                            DecimalFormat df = new DecimalFormat("#.##");

                            model.addAttribute("queryResults", webResult);
                            model.addAttribute("query", s);
                            model.addAttribute("querysql", s);
                            if (queryCount.equals("Y")) {
                                model.addAttribute("queryResultCount", webResult.getRows().size());
                            }

                            if (elapsedTime.equals("Y")) {
                                model.addAttribute("elapsedTimeResult", df.format(timeTaken / 1000));
                            }

                            addCommandToHistory(session, userPrefs, s);

                        }
                    } catch (Exception ex) {
                        result.setCommand(s);
                        result.setMessage(ex.getMessage() == null ? "ERROR: Unable to run query" : "ERROR: " + ex.getMessage());
                        result.setRows(-1);
                        model.addAttribute("result", result);
                        model.addAttribute("query", s);
                        logger.info("Error Result = " + result);
                    }
                }
                else
                {
                    model.addAttribute("query", s);

                    if (s.length() > 0) {
                        if (determineQueryType(s).equals("COMMIT")) {
                            result = genericDAO.runStatement("commit", elapsedTime, "Y", (String)session.getAttribute("user_key"));
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        } else if (determineQueryType(s).equals("ROLLBACK")) {
                            result = genericDAO.runStatement("rollback", elapsedTime, "Y", (String)session.getAttribute("user_key"));

                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        }
                        else if (determineQueryType(s).equals("CALL"))
                        {
                            result = genericDAO.runStatement
                                    (s, elapsedTime, "Y", (String)session.getAttribute("user_key"));
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        }
                        else if (determineQueryType(s).equals("DESCRIBE"))
                        {
                            try
                            {
                                webResult = genericDAO.runGenericQuery
                                        (s, null, (String)session.getAttribute("user_key"), -1);
                                model.addAttribute("queryResults", webResult);
                                model.addAttribute("query", s);
                                model.addAttribute("querysql", s);
                                addCommandToHistory(session, userPrefs, s);
                            }
                            catch (Exception ex)
                            {
                                result.setCommand(s);
                                result.setMessage(ex.getMessage() == null ? "ERROR: Unable to run query" : "ERROR: " + ex.getMessage());
                                result.setRows(-1);
                                model.addAttribute("result", result);
                                model.addAttribute("query", s);
                                //logger.info("Error Result = " + result);
                            }

                        }
                        else
                        {
                            result = genericDAO.runStatement
                                    (s, elapsedTime, "N", (String)session.getAttribute("user_key"));

                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                                session.setAttribute("schemaMap",
                                                genericDAO.populateSchemaMap
                                                        ((String)session.getAttribute("schema"),
                                                        (String)session.getAttribute("user_key")));
                            }
                        }

                    }

                }
            }
            else
            {
                model.addAttribute("query", query);
                logger.info("multiple SQL statements need to be executed");
                SortedMap<String, Object> queryResults =
                        handleMultipleStatements(splitQueryStr,
                                userPrefs,
                                queryCount,
                                elapsedTime,
                                explainPlan,
                                session);
                logger.info("keys : " + queryResults.keySet());
                model.addAttribute("sqlResultMap", queryResults);
                model.addAttribute("statementsExecuted", queryResults.size());
            }
        }

        model.addAttribute("queryCount", queryCount);
        model.addAttribute("elapsedTime", elapsedTime);
        model.addAttribute("explainPlan", explainPlan);

        return "query";
    }

    @RequestMapping(value = "/uploadsql", method = RequestMethod.POST)
    public String fileuploadRequest
            (Model model,
             @RequestParam("file") MultipartFile file,
             HttpServletRequest request) throws Exception
    {

        if (!file.isEmpty())
        {

                byte[] bytes = file.getBytes();
                String data = new String(bytes);

                model.addAttribute("query", data);
                logger.info("Loaded SQL file with " + data.length() + " bytes");
        }

        model.addAttribute("queryCount", "N");
        model.addAttribute("elapsedTime", "N");
        model.addAttribute("explainPlan", "N");

        return "query";
    }

    @RequestMapping(value = "/executequery", method = RequestMethod.GET)
    public String executeQuery
            (Model model,
             HttpServletResponse response,
             HttpServletRequest request,
             HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        logger.info("Received request to action a query directly");
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        UserPref userPrefs = (UserPref) session.getAttribute("prefs");

        String query = request.getParameter("query");
        logger.info("Query = " + query);

        CommandResult result = new CommandResult();
        String s = query.trim();
        model.addAttribute("query", s);

        try
        {
            WebResult webResult = genericDAO.runGenericQuery
                    (query, null, (String)session.getAttribute("user_key"), userPrefs.getMaxRecordsinSQLQueryWindow());

            logger.info("Query run");
            model.addAttribute("queryResults", webResult);
            model.addAttribute("querysql", query);
            addCommandToHistory(session, userPrefs, query);

        }
        catch (Exception ex)
        {
            result.setCommand(query);
            result.setMessage(ex.getMessage() == null ? "Unable to run query" : ex.getMessage());
            result.setRows(-1);
            model.addAttribute("result", result);
        }

        model.addAttribute("queryCount", "N");
        model.addAttribute("elapsedTime", "N");
        model.addAttribute("explainPlan", "N");

        return "query";
    }

    private String[] spiltQuery (String query)
    {
        Pattern pattern = Pattern.compile(";\\s", Pattern.MULTILINE);
        String [] splitQueryStr = pattern.split(query);

        logger.info("split query = {" + Arrays.toString(splitQueryStr) + "}");
        return splitQueryStr;
    }

    private String determineQueryType (String query)
    {
        String sQuery = query.toLowerCase().trim();

        if (sQuery.startsWith("select") || sQuery.startsWith("show"))
        {
            return decodeType(0);
        }
        else if (sQuery.startsWith("insert"))
        {
            return decodeType(1);
        }
        else if (sQuery.startsWith("delete"))
        {
            return decodeType(2);
        }
        else if (sQuery.startsWith("alter"))
        {
            return decodeType(3);
        }
        else if (sQuery.startsWith("update"))
        {
            return decodeType(4);
        }
        else if (sQuery.startsWith("call"))
        {
            return decodeType(5);
        }
        else if (sQuery.equals("commit;") || sQuery.equals("commit"))
        {
            return decodeType(6);
        }
        else if (sQuery.equals("rollback;") || sQuery.equals("rollback"))
        {
            return decodeType(7);
        }
        else if (sQuery.startsWith("describe"))
        {
            return decodeType(8);
        }
        else
        {
            return decodeType(3);
        }
    }

    private String decodeType(int type)
    {
        return QUERY_TYPES[type];
    }

    private String checkForComments(String s)
    {
        if (s.startsWith("--"))
        {
            int index = s.indexOf("\n");
            if (index != -1)
            {
                String newQuery = s.substring(s.indexOf("\n"));
                if (newQuery.trim().startsWith("--"))
                {
                    return checkForComments(newQuery.trim());
                }
                else
                {
                    return newQuery.trim();
                }
            }
            else
            {
                return "";
            }
        }
        else
        {
            return s;
        }

    }

    private void addCommandToHistory (HttpSession session, UserPref prefs, String sql)
    {
        @SuppressWarnings("unchecked")
        LinkedList<String> historyList = (LinkedList<String>) session.getAttribute("history");

        int maxsize = prefs.getHistorySize();

        if (historyList.size() == maxsize)
        {
            historyList.remove((maxsize - 1));
            historyList.addFirst(sql);
        }
        else
        {
            historyList.addFirst(sql);
        }

    }

    private SortedMap<String, Object> handleMultipleStatements
            (String[] splitQueryStr,
             UserPref userPrefs,
             String queryCount,
             String elapsedTime,
             String explainPlan,
             HttpSession session) throws SQLException, PivotalMySQLWebException {
        int counter = 9000;
        boolean ddl = false;

        SortedMap<String, Object> queryResults = new TreeMap<String, Object>();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        for (String nextQuery: splitQueryStr)
        {
            CommandResult result = new CommandResult();
            List queryResult = new ArrayList();

            String s = checkForComments(nextQuery.trim());
            s = s.trim();

            if (determineQueryType(s).equals("SELECT") || determineQueryType(s).equals("DESCRIBE"))
            {
                WebResult webResult = null;
                try
                {
                    long start = System.currentTimeMillis();
                    webResult = genericDAO.runGenericQuery
                                    (s, null, (String)session.getAttribute("user_key"), userPrefs.getMaxRecordsinSQLQueryWindow());
                    long end = System.currentTimeMillis();

                    double timeTaken = new Double(end - start).doubleValue();
                    DecimalFormat df = new DecimalFormat("#.##");

                    queryResult.add(s);
                    queryResult.add(webResult);

                    if (elapsedTime.equals("Y"))
                    {
                        queryResult.add(df.format(timeTaken/1000));
                    }
                    else
                    {
                        queryResult.add(null);
                    }

                    queryResults.put(counter + "SELECT", queryResult);
                    addCommandToHistory(session, userPrefs, s);
                }
                catch (Exception ex)
                {
                    result.setCommand(s);
                    result.setMessage(ex.getMessage() == null ? "ERROR: Unable to run query" : "ERROR: " + ex.getMessage());
                    result.setRows(-1);
                    queryResults.put(counter + "QUERYERROR", result);
                    //conn.rollback();
                }
                counter++;
            }
            else
            {
                if (s.length() > 0)
                {
                    if (determineQueryType(s).equals("COMMIT"))
                    {
                        result = genericDAO.runStatement("commit", elapsedTime, "Y", (String)session.getAttribute("user_key"));
                    }
                    else if (determineQueryType(s).equals("ROLLBACK"))
                    {
                        result = genericDAO.runStatement("rollback", elapsedTime, "Y", (String)session.getAttribute("user_key"));
                    }
                    else
                    {
                        result = genericDAO.runStatement
                                (s, elapsedTime, isDDL(s), (String)session.getAttribute("user_key"));
                    }

                    ddl = false;
                    if (determineQueryType(s).equals("INSERT"))
                    {
                        queryResults.put(counter + "INSERT", result);
                    }
                    else if (determineQueryType(s).equals("UPDATE"))
                    {
                        queryResults.put(counter + "UPDATE", result);
                    }
                    else if (determineQueryType(s).equals("DELETE"))
                    {
                        queryResults.put(counter + "DELETE", result);
                    }
                    else if (determineQueryType(s).equals("COMMIT"))
                    {
                        queryResults.put(counter + "COMMIT", result);
                    }
                    else if (determineQueryType(s).equals("ROLLBACK"))
                    {
                        queryResults.put(counter + "ROLLBACK", result);
                    }
                    else
                    {
                        queryResults.put(counter + "DDL", result);
                        ddl = true;
                    }

                    if (result.getMessage().startsWith("SUCCESS"))
                    {
                        addCommandToHistory(session, userPrefs, s);
                    }

                    counter++;
                }
            }

        }

        if (ddl == true)
        {
            session.setAttribute("schemaMap",
                        genericDAO.populateSchemaMap
                                    ((String)session.getAttribute("schema"),
                                    (String)session.getAttribute("user_key")));
        }

        return queryResults;
    }

    private String isDDL(String s)
    {
        if (determineQueryType(s).equals("INSERT") ||
            determineQueryType(s).equals("UPDATE") ||
            determineQueryType(s).equals("DELETE")){
            return "N";
        }
        else{
            return "Y";
        }
    }
}
