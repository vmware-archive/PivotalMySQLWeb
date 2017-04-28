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
public class HomeController
{
    protected static Logger logger = Logger.getLogger(HomeController.class);

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String login(Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        logger.info("Received request to show home page");

        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        javax.servlet.jsp.jstl.sql.Result databaseList;

        // retrieve connection
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = cm.getConnection(session.getId());

        databaseList = QueryUtil.runQuery(conn,
                                         "SELECT SCHEMA_NAME 'database', default_character_set_name 'charset', DEFAULT_COLLATION_NAME 'collation' FROM information_schema.SCHEMATA",
                                         -1);

        model.addAttribute("databaseList", databaseList);

        return "main";
    }

}
