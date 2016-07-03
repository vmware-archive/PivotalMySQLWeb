package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.Map;

@Controller
public class RefreshController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public String refreshPage
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

        logger.info("Received request refresh schema object list");

        Map schemaMap = (Map) session.getAttribute("schemaMap");
        Connection conn = AdminUtil.getConnection((String)session.getAttribute("user_key"));

        // get schema count now
        schemaMap = QueryUtil.populateSchemaMap
                (conn, schemaMap, (String) session.getAttribute("schema"));

        session.setAttribute("schemaMap", schemaMap);


        return "main";
    }
}
