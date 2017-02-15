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

import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
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
public class ConmapController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/viewconmap", method = RequestMethod.GET)
    public String viewConnections
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

        logger.info("Received request to show connection map");

        ConnectionManager cm = ConnectionManager.getInstance();

        String conMapAction = request.getParameter("conMapAction");
        String key = request.getParameter("key");

        if (conMapAction != null)
        {
            logger.info("conMapAction = " + conMapAction);
            logger.info("key = " + key);

            if (conMapAction.equalsIgnoreCase("DELETE"))
            {
                // remove this connection from Map and close it.
                cm.removeConnection(key);
                logger.info("Connection closed for key " + key);
                model.addAttribute("saved", "Successfully closed connection with key " + key);
            }
        }

        model.addAttribute("conmap", cm.getConnectionMap());
        model.addAttribute("conmapsize", cm.getConnectionListSize());

        return "conmap";
    }
}
