package com.pivotal.pcf.mysqlweb.controller;

import java.sql.Connection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HistoryController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String showHistory
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

        logger.info("Received request to show command history");
        UserPref userPref = (UserPref) session.getAttribute("prefs");

        String histAction = request.getParameter("histAction");

        if (histAction != null)
        {
            logger.info("histAction = " + histAction);
            // clear history
            session.setAttribute("history", new LinkedList());

            model.addAttribute("historyremoved", "Successfully cleared history list");
        }

        LinkedList historyList = (LinkedList) session.getAttribute("history");

        int maxsize = userPref.getHistorySize();

        model.addAttribute("historyList", historyList.toArray());
        model.addAttribute("historysize", historyList.size());

        // This will resolve to /WEB-INF/jsp/history.jsp
        return "history";
    }
}
