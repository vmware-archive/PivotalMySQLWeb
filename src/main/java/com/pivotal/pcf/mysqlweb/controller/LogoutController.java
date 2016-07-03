package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.Login;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LogoutController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout
            (Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception
    {
        logger.info("Received request to logout of PostgreSQL*Web");

        // remove connection from list
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.removeConnection(session.getId());

        session.invalidate();

        model.addAttribute("loginObj", new Login("", "", "jdbc:postgresql://localhost:5432/apples", "apples"));
        return "login";

    }

}
