package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
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

@Controller
public class UserController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userDetails
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

        logger.info("Received request to show user information");

        javax.servlet.jsp.jstl.sql.Result processList, privsList;

        // retrieve connection
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = cm.getConnection(session.getId());

        privsList = QueryUtil.runQuery(conn, "SHOW PRIVILEGES", -1);
        processList = QueryUtil.runQuery(conn, "SHOW processlist", -1);

        model.addAttribute("privsList", privsList);
        model.addAttribute("processList", processList);

        return "info";
    }
}
