package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.dao.tables.Table;
import com.pivotal.pcf.mysqlweb.dao.tables.TableDAO;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class TableController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public String showTables
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
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

        String schema = null;
        javax.servlet.jsp.jstl.sql.Result tableStructure, tableDetails, tableIndexes;

        logger.info("Received request to show tables");

        TableDAO tableDAO = PivotalMySQLWebDAOFactory.getTableDAO();

        String selectedSchema = request.getParameter("selectedSchema");
        logger.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String) session.getAttribute("schema");
        }

        logger.info("schema = " + schema);

        String tabAction = request.getParameter("tabAction");
        Result result = new Result();

        if (tabAction != null)
        {
            logger.info("tabAction = " + tabAction);
            result = null;

            if (tabAction.equalsIgnoreCase("STRUCTURE"))
            {

                tableStructure =
                        tableDAO.getTableStructure
                                (schema,
                                        (String)request.getParameter("tabName"),
                                        (String)session.getAttribute("user_key"));


                model.addAttribute("tableStructure", tableStructure);
                model.addAttribute("tablename", (String)request.getParameter("tabName"));
            }
            else if (tabAction.equalsIgnoreCase("DETAILS"))
            {
                tableDetails =
                        tableDAO.getTableDetails
                                (schema,
                                        (String)request.getParameter("tabName"),
                                        (String)session.getAttribute("user_key"));


                model.addAttribute("tableDetails", tableDetails);
                model.addAttribute("tablename", (String)request.getParameter("tabName"));
            }
            else if (tabAction.equalsIgnoreCase("DDL"))
            {
                String ddl = tableDAO.runShowQuery(schema,
                                                   (String)request.getParameter("tabName"),
                                                   (String)session.getAttribute("user_key"));

                model.addAttribute("tableDDL", ddl.trim());
                model.addAttribute("tablename", (String)request.getParameter("tabName"));
            }
            else if (tabAction.equalsIgnoreCase("INDEXES"))
            {
                tableIndexes = tableDAO.showIndexes(schema,
                        (String)request.getParameter("tabName"),
                        (String)session.getAttribute("user_key"));

                model.addAttribute("tableIndexes", tableIndexes);
                model.addAttribute("tablename", (String)request.getParameter("tabName"));
            }
            else
            {
                result =
                        tableDAO.simpletableCommand
                                (schema,
                                        (String)request.getParameter("tabName"),
                                        tabAction,
                                        (String)session.getAttribute("user_key"));
                model.addAttribute("result", result);
            }
        }

        List<Table> tbls = tableDAO.retrieveTableList
                  (schema, null, (String)session.getAttribute("user_key"));

        model.addAttribute("records", tbls.size());
        model.addAttribute("estimatedrecords", tbls.size());
        model.addAttribute("tables", tbls);

        model.addAttribute("schemas",
                PivotalMySQLWebDAOUtil.getAllSchemas
                        ((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "tables";
    }

    @RequestMapping(value = "/tables", method = RequestMethod.POST)
    public String performTableAction
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
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

        String schema = null;
        Result result = new Result();
        List<Table> tbls = null;

        logger.info("Received request to perform an action on the tables");

        TableDAO tableDAO = PivotalMySQLWebDAOFactory.getTableDAO();

        String selectedSchema = request.getParameter("selectedSchema");
        logger.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String) session.getAttribute("schema");
        }

        logger.info("schema = " + schema);

        if (request.getParameter("searchpressed") != null)
        {
            tbls = tableDAO.retrieveTableList
                            (schema,
                            (String)request.getParameter("search"),
                            (String)session.getAttribute("user_key"));

            model.addAttribute("search", (String)request.getParameter("search"));
        }
        else
        {
            String[] tableList  = request.getParameterValues("selected_tbl[]");
            String   commandStr = request.getParameter("submit_mult");

            logger.info("tableList = " + Arrays.toString(tableList));
            logger.info("command = " + commandStr);

            // start actions now if tableList is not null

            if (tableList != null)
            {
                List al = new ArrayList<Result>();
                for (String table: tableList)
                {
                    result = null;
                    result = tableDAO.simpletableCommand
                            (schema,
                                    table,
                                    commandStr,
                                    (String)session.getAttribute("user_key"));

                    al.add(result);
                }

                model.addAttribute("arrayresult", al);
            }

            tbls = tableDAO.retrieveTableList
                            (schema, null, (String)session.getAttribute("user_key"));
        }

        model.addAttribute("records", tbls.size());
        model.addAttribute("estimatedrecords", tbls.size());
        model.addAttribute("tables", tbls);

        model.addAttribute("schemas",
                PivotalMySQLWebDAOUtil.getAllSchemas
                        ((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "tables";
    }
}
