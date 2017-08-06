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
import com.pivotal.pcf.mysqlweb.dao.constraints.Constraint;
import com.pivotal.pcf.mysqlweb.dao.constraints.ConstraintDAO;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
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
public class ConstraintController
{
    protected static Logger logger = Logger.getLogger(ConstraintController.class);

    @RequestMapping(value = "/constraints", method = RequestMethod.GET)
    public String showConstraints
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        String schema = null;

        logger.info("Received request to show constraints");

        ConstraintDAO constraintDAO = PivotalMySQLWebDAOFactory.getConstraintDAO();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

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

        String constraintAction = request.getParameter("constraintAction");
        Result result = new Result();

        if (constraintAction != null)
        {
            logger.info("constraintAction = " + constraintAction);
            result = null;

            if (constraintAction.equals("DROP"))
            {
                result =
                        constraintDAO.simpleconstraintCommand
                                (schema,
                                (String) request.getParameter("constraintName"),
                                (String) request.getParameter("tableName"),
                                (String) request.getParameter("constraintType"),
                                constraintAction,
                                (String) session.getAttribute("user_key"));
                model.addAttribute("result", result);
            }
        }

        List<Constraint> constraints = constraintDAO.retrieveConstraintList
                (schema, null, (String)session.getAttribute("user_key"));

        model.addAttribute("records", constraints.size());
        model.addAttribute("estimatedrecords", constraints.size());
        model.addAttribute("constraints", constraints);

        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "constraints";

    }

    @RequestMapping(value = "/constraints", method = RequestMethod.POST)
    public String performConstraintAction
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {

        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        String schema = null;
        Result result = new Result();
        List<Constraint> constraints = null;

        logger.info("Received request to perform an action on the constraints");

        ConstraintDAO constraintDAO = PivotalMySQLWebDAOFactory.getConstraintDAO();
        GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

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
            constraints = constraintDAO.retrieveConstraintList
                    (schema,
                    (String)request.getParameter("search"),
                    (String)session.getAttribute("user_key"));

            model.addAttribute("search", (String)request.getParameter("search"));
        }
        else
        {
            String[] tableList  = request.getParameterValues("selected_constraint[]");
            String   commandStr = request.getParameter("submit_mult");

            logger.info("tableList = " + Arrays.toString(tableList));
            logger.info("command = " + commandStr);

            // start actions now if tableList is not null

            if (tableList != null)
            {
                List al = new ArrayList<Result>();
                for (String constraint: tableList)
                {
                    result = null;
                    result = constraintDAO.simpleconstraintCommand
                            (schema,
                             constraint,
                             commandStr,
                             "",
                             "",
                             (String)session.getAttribute("user_key"));

                    al.add(result);
                }

                model.addAttribute("arrayresult", al);
            }

            constraints = constraintDAO.retrieveConstraintList
                    (schema, null, (String)session.getAttribute("user_key"));

        }

        model.addAttribute("records", constraints.size());
        model.addAttribute("estimatedrecords", constraints.size());
        model.addAttribute("constraints", constraints);

        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "constraints";
    }
}
