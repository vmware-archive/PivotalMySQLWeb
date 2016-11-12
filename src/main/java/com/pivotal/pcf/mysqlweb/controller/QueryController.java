package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.CommandResult;
import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
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
    protected static Logger logger = Logger.getLogger("controller");
    private static final String FILENAME = "worksheet.sql";
    private static final String FILENAME_EXPORT = "query-output.csv";
    private static final String FILENAME_EXPORT_JSON = "query-output.json";
    private static final String SAVE_CONTENT_TYPE = "application/x-download";
    private final String QUERY_TYPES[] = {
            "SELECT", "INSERT", "DELETE", "DDL", "UPDATE", "CALL", "COMMIT", "ROLLBACK"
    };

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String worksheet
            (Model model, HttpServletResponse response,
             HttpServletRequest request,
             HttpSession session) throws Exception
    {
        if (session.getAttribute("user_key") == null)
        {
            logger.info("user_key is null new Login required");
            response.sendRedirect("/");
            return null;
        }
        else
        {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null )
            {
                response.sendRedirect("/");
                return null;
            }
            else
            {
                if (conn.isClosed())
                {
                    response.sendRedirect("/");
                    return null;
                }
            }

        }

        logger.info("Received request to show query worksheet");
        UserPref userPrefs = (UserPref) session.getAttribute("prefs");

        String action = request.getParameter("action");
        if (action != null)
        {
            CommandResult result = new CommandResult();
            ConnectionManager cm = ConnectionManager.getInstance();
            Connection conn = cm.getConnection(session.getId());

            if (action.trim().equals("commit"))
            {
                logger.info("commit action requested");
                result = QueryUtil.runCommitOrRollback(conn, true, "N");
                addCommandToHistory(session, userPrefs, "commit");
                logger.info("COMMIT RESULT " + result);

                model.addAttribute("result", result);
            }
            else if (action.trim().equals("rollback"))
            {
                logger.info("rollback action requested");
                result = QueryUtil.runCommitOrRollback(conn, false, "N");
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
        if (session.getAttribute("user_key") == null)
        {
            logger.info("user_key is null new Login required");
            response.sendRedirect("/");
            return null;
        }
        else
        {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null )
            {
                response.sendRedirect("/");
                return null;
            }
            else
            {
                if (conn.isClosed())
                {
                    response.sendRedirect("/");
                    return null;
                }
            }

        }

        logger.info("Received request to action SQL from query worksheet");
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = cm.getConnection(session.getId());

        UserPref userPrefs = (UserPref) session.getAttribute("prefs");

        logger.info("Query = [" + query + "]");
        String [] splitQueryStr = spiltQuery(query);

        CommandResult result = new CommandResult();

        if (query.trim().length() != 0)
        {
            if (splitQueryStr.length == 1)
            {
                String s = checkForComments(query);
                s = s.trim();

                if (determineQueryType(s).equals("SELECT")) {
                    try {
                        if (explainPlan.equals("Y")) {
                            logger.info("Need to run explain plan");
                            model.addAttribute("explainresult", QueryUtil.runExplainPlan(conn, query));
                            model.addAttribute("query", s);
                        } else {
                            long start = System.currentTimeMillis();
                            Result res = QueryUtil.runQuery(conn, s, userPrefs.getMaxRecordsinSQLQueryWindow());
                            long end = System.currentTimeMillis();

                            double timeTaken = new Double(end - start).doubleValue();
                            DecimalFormat df = new DecimalFormat("#.##");

                            model.addAttribute("queryResults", res);
                            model.addAttribute("query", s);
                            model.addAttribute("querysql", s);
                            if (queryCount.equals("Y")) {
                                model.addAttribute("queryResultCount", res.getRowCount());
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
                        //conn.rollback();
                    }
                }
                else
                {
                    model.addAttribute("query", s);

                    if (s.length() > 0) {
                        if (determineQueryType(s).equals("COMMIT")) {
                            result = QueryUtil.runCommitOrRollback(conn, true, elapsedTime);
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        } else if (determineQueryType(s).equals("ROLLBACK")) {
                            result = QueryUtil.runCommitOrRollback(conn, false, elapsedTime);
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        } else if (determineQueryType(s).equals("CALL")) {
                            result = QueryUtil.runCommand(conn, s, elapsedTime);
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
                            }
                        } else {
                            result = QueryUtil.runCommand(conn, s, elapsedTime);
                            model.addAttribute("result", result);
                            if (result.getMessage().startsWith("SUCCESS")) {
                                addCommandToHistory(session, userPrefs, s);
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
                                conn,
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
        if (session.getAttribute("user_key") == null)
        {
            logger.info("user_key is null new Login required");
            response.sendRedirect("/");
            return null;
        }
        else
        {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null )
            {
                response.sendRedirect("/");
                return null;
            }
            else
            {
                if (conn.isClosed())
                {
                    response.sendRedirect("/");
                    return null;
                }
            }

        }

        logger.info("Received request to action a query directly");

        UserPref userPrefs = (UserPref) session.getAttribute("prefs");
        ConnectionManager cm = ConnectionManager.getInstance();
        // retrieve connection
        Connection conn = cm.getConnection(session.getId());

        String query = request.getParameter("query");
        logger.debug("Query = " + query);

        CommandResult result = new CommandResult();
        String s = query.trim();
        model.addAttribute("query", s);

        try
        {
            Result res = QueryUtil.runQuery(conn, query, userPrefs.getMaxRecordsinSQLQueryWindow());
            logger.info("Query run");
            model.addAttribute("queryResults", res);
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

        if (sQuery.startsWith("select"))
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
             Connection conn,
             UserPref userPrefs,
             String queryCount,
             String elapsedTime,
             String explainPlan,
             HttpSession session) throws SQLException
    {
        int counter = 9000;

        SortedMap<String, Object> queryResults = new TreeMap<String, Object>();

        for (String nextQuery: splitQueryStr)
        {
            CommandResult result = new CommandResult();
            List queryResult = new ArrayList();

            String s = checkForComments(nextQuery.trim());
            s = s.trim();

            if (determineQueryType(s).equals("SELECT"))
            {
                Result res = null;
                try
                {
                    long start = System.currentTimeMillis();
                    res = QueryUtil.runQuery(conn, s, userPrefs.getMaxRecordsinSQLQueryWindow());
                    long end = System.currentTimeMillis();

                    double timeTaken = new Double(end - start).doubleValue();
                    DecimalFormat df = new DecimalFormat("#.##");

                    queryResult.add(s);
                    queryResult.add(res);

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
                        result = QueryUtil.runCommitOrRollback(conn, true, elapsedTime);
                    }
                    else if (determineQueryType(s).equals("ROLLBACK"))
                    {
                        result = QueryUtil.runCommitOrRollback(conn, false, elapsedTime);
                    }
                    else
                    {
                        result = QueryUtil.runCommand(conn, s, elapsedTime);
                    }

                    if (result.getMessage().startsWith("SUCCESS"))
                    {
                        addCommandToHistory(session, userPrefs, s);
                    }

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
                    }

                    counter++;
                }
            }

        }

        return queryResults;
    }
}
