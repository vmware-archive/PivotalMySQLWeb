package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOUtil;
import com.pivotal.pcf.mysqlweb.dao.indexes.Index;
import com.pivotal.pcf.mysqlweb.dao.indexes.IndexDAO;
import com.pivotal.pcf.mysqlweb.dao.tables.Table;
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
public class IndexController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/indexes", method = RequestMethod.GET)
    public String showIndexes
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception {
        if (session.getAttribute("user_key") == null) {
            logger.info("user_key is null new Login required");
            response.sendRedirect("/");
            return null;
        } else {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null) {
                response.sendRedirect("/");
                return null;
            } else {
                if (conn.isClosed()) {
                    response.sendRedirect("/");
                    return null;
                }
            }

        }

        String schema = null;
        javax.servlet.jsp.jstl.sql.Result indexStructure;

        logger.info("Received request to show indexes");

        IndexDAO indexDAO = PivotalMySQLWebDAOFactory.getIndexDAO();

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

        String idxAction = request.getParameter("idxAction");
        Result result = new Result();

        if (idxAction != null)
        {
            logger.debug("idxAction = " + idxAction);
            result = null;

            result =
                    indexDAO.simpleindexCommand
                            (schema,
                                    (String)request.getParameter("idxName"),
                                    idxAction,
                                    (String)session.getAttribute("user_key"));
                model.addAttribute("result", result);
        }

        List<Index> indexes = indexDAO.retrieveIndexList
                (schema, null, (String)session.getAttribute("user_key"));

        model.addAttribute("records", indexes.size());
        model.addAttribute("estimatedrecords", indexes.size());
        model.addAttribute("indexes", indexes);

        model.addAttribute("schemas",
                PivotalMySQLWebDAOUtil.getAllSchemas
                        ((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "indexes";
    }

    @RequestMapping(value = "/indexes", method = RequestMethod.POST)
    public String performIndexAction
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
        List<Index> indexes = null;

        logger.info("Received request to perform an action on the indexes");

        IndexDAO indexDAO = PivotalMySQLWebDAOFactory.getIndexDAO();

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
            indexes = indexDAO.retrieveIndexList
                    (schema,
                            (String)request.getParameter("search"),
                            (String)session.getAttribute("user_key"));

            model.addAttribute("search", (String)request.getParameter("search"));
        }
        else
        {
            String[] tableList  = request.getParameterValues("selected_idx[]");
            String   commandStr = request.getParameter("submit_mult");

            logger.info("tableList = " + Arrays.toString(tableList));
            logger.info("command = " + commandStr);

            // start actions now if tableList is not null

            if (tableList != null)
            {
                List al = new ArrayList<Result>();
                for (String index: tableList)
                {
                    result = null;
                    result = indexDAO.simpleindexCommand
                            (schema,
                             index,
                             commandStr,
                             (String)session.getAttribute("user_key"));

                    al.add(result);
                }

                model.addAttribute("arrayresult", al);
            }

            indexes = indexDAO.retrieveIndexList
                    (schema, null, (String)session.getAttribute("user_key"));

        }

        model.addAttribute("records", indexes.size());
        model.addAttribute("estimatedrecords", indexes.size());
        model.addAttribute("indexes", indexes);

        model.addAttribute("schemas",
                PivotalMySQLWebDAOUtil.getAllSchemas
                        ((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "indexes";

    }

}