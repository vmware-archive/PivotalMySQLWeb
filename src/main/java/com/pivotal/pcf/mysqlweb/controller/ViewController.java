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

import com.pivotal.pcf.mysqlweb.beans.Result;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.dao.views.View;
import com.pivotal.pcf.mysqlweb.dao.views.ViewDAO;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ViewController
{
    protected static Logger logger = Logger.getLogger(ViewController.class);

    @RequestMapping(value = "/views", method = RequestMethod.GET)
    public String showViews
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        int startAtIndex = 0, endAtIndex = 0;
        String schema = null;

        logger.info("Received request to show views");

        ViewDAO viewDAO = PivotalMySQLWebDAOFactory.getViewDAO();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        Result result = new Result();

        String viewAction = request.getParameter("viewAction");
        String selectedSchema = request.getParameter("selectedSchema");
        logger.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String)session.getAttribute("schema");
        }

        logger.info("schema = " + schema);

        if (viewAction != null)
        {
            logger.info("viewAction = " + viewAction);

            if (viewAction.equals("DEF"))
            {
                String def =
                        viewDAO.getViewDefinition
                                (schema,
                                        (String)request.getParameter("viewName"),
                                        (String)session.getAttribute("user_key"));

                model.addAttribute("viewName", (String)request.getParameter("viewName"));
                model.addAttribute("viewdef", def);
            }
            else
            {
                result = null;
                result =
                        viewDAO.simpleviewCommand
                                (schema,
                                        (String)request.getParameter("viewName"),
                                        viewAction,
                                        (String)session.getAttribute("user_key"));

                model.addAttribute("result", result);

                if (result.getMessage().startsWith("SUCCESS"))
                {
                    if (viewAction.equalsIgnoreCase("DROP"))
                    {
                        session.setAttribute("schemaMap",
                                        genericDAO.populateSchemaMap
                                                ((String)session.getAttribute("schema"),
                                                (String)session.getAttribute("user_key")));
                    }
                }
            }
        }

        List<View> views = viewDAO.retrieveViewList
                (schema,
                        null,
                        (String)session.getAttribute("user_key"));

        model.addAttribute("records", views.size());
        model.addAttribute("estimatedrecords", views.size());
        model.addAttribute("views", views);

        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "views";
    }

    @RequestMapping(value = "/views", method = RequestMethod.POST)
    public String performViewAction
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        String schema = null;
        int startAtIndex = 0, endAtIndex = 0;

        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        Result result = new Result();
        List<View> views = null;

        logger.info("Received request to perform an action on the views");

        String selectedSchema = request.getParameter("selectedSchema");
        logger.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String)session.getAttribute("schema");
        }

        logger.info("schema = " + schema);

        ViewDAO viewDAO = PivotalMySQLWebDAOFactory.getViewDAO();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        if (request.getParameter("searchpressed") != null)
        {
            views = viewDAO.retrieveViewList
                    (schema,
                            (String)request.getParameter("search"),
                            (String)session.getAttribute("user_key"));

            model.addAttribute("search", (String)request.getParameter("search"));
        }
        else
        {
            String[] tableList  = request.getParameterValues("selected_view[]");
            String   commandStr = request.getParameter("submit_mult");

            logger.info("tableList = " + Arrays.toString(tableList));
            logger.info("command = " + commandStr);

            // start actions now if tableList is not null

            if (tableList != null)
            {
                List al = new ArrayList<Result>();
                for (String view: tableList)
                {
                    result = null;
                    result =
                            viewDAO.simpleviewCommand
                                    (schema,
                                            view,
                                            commandStr,
                                            (String)session.getAttribute("user_key"));
                    al.add(result);
                }

                model.addAttribute("arrayresult", al);
            }

            views = viewDAO.retrieveViewList
                    (schema,
                            null,
                            (String)session.getAttribute("user_key"));

        }

        model.addAttribute("records", views.size());
        model.addAttribute("estimatedrecords", views.size());
        model.addAttribute("views", views);
        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "views";
    }
}
