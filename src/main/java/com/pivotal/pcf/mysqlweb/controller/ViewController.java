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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class ViewController
{
    
    @GetMapping(value = "/views")
    public String showViews
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            log.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        String schema = null;

        log.info("Received request to show views");

        ViewDAO viewDAO = PivotalMySQLWebDAOFactory.getViewDAO();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

        Result result = new Result();

        String viewAction = request.getParameter("viewAction");
        String selectedSchema = request.getParameter("selectedSchema");
        log.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String)session.getAttribute("schema");
        }

        log.info("schema = " + schema);

        if (viewAction != null)
        {
            log.info("viewAction = " + viewAction);

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

    @PostMapping(value = "/views")
    public String performViewAction
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        String schema = null;

        if (Utils.verifyConnection(response, session))
        {
            log.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        Result result = new Result();
        List<View> views = null;

        log.info("Received request to perform an action on the views");

        String selectedSchema = request.getParameter("selectedSchema");
        log.info("selectedSchema = " + selectedSchema);

        if (selectedSchema != null)
        {
            schema = selectedSchema;
        }
        else
        {
            schema = (String)session.getAttribute("schema");
        }

        log.info("schema = " + schema);

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

            log.info("tableList = " + Arrays.toString(tableList));
            log.info("command = " + commandStr);

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
