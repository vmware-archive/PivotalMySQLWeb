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
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.dao.indexes.Index;
import com.pivotal.pcf.mysqlweb.dao.indexes.IndexDAO;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
public class IndexController
{
    protected static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(value = "/indexes")
    public String showIndexes
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception {

        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        String schema = null;
        WebResult indexStructure;

        logger.info("Received request to show indexes");

        IndexDAO indexDAO = PivotalMySQLWebDAOFactory.getIndexDAO();
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

        String idxAction = request.getParameter("idxAction");
        Result result = new Result();

        if (idxAction != null)
        {
            logger.info("idxAction = " + idxAction);
            result = null;

            if (idxAction.equals("STRUCTURE"))
            {
                indexStructure =
                        indexDAO.getIndexDetails
                                (schema,
                                 (String)request.getParameter("tabName"),
                                 (String)request.getParameter("idxName"),
                                 (String)session.getAttribute("user_key"));

                model.addAttribute("indexStructure", indexStructure);
                model.addAttribute("indexname", (String)request.getParameter("idxName"));
            }
            else
            {
                result =
                        indexDAO.simpleindexCommand
                                (schema,
                                        (String) request.getParameter("idxName"),
                                        idxAction,
                                        (String) request.getParameter("tableName"),
                                        (String) session.getAttribute("user_key"));
                model.addAttribute("result", result);

                if (result.getMessage().startsWith("SUCCESS"))
                {
                    if (idxAction.equalsIgnoreCase("DROP"))
                    {
                        session.setAttribute("schemaMap",
                                            genericDAO.populateSchemaMap
                                                ((String)session.getAttribute("schema"),
                                                (String)session.getAttribute("user_key")));
                    }
                }
            }
        }

        List<Index> indexes = indexDAO.retrieveIndexList
                (schema, null, (String)session.getAttribute("user_key"));

        model.addAttribute("records", indexes.size());
        model.addAttribute("estimatedrecords", indexes.size());
        model.addAttribute("indexes", indexes);

        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "indexes";
    }

    @PostMapping(value = "/indexes")
    public String performIndexAction
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            logger.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        String schema = null;
        Result result = new Result();
        List<Index> indexes = null;

        logger.info("Received request to perform an action on the indexes");

        IndexDAO indexDAO = PivotalMySQLWebDAOFactory.getIndexDAO();
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
                List<Result> al = new ArrayList<>();
                for (String index: tableList)
                {
                    result = null;
                    result = indexDAO.simpleindexCommand
                            (schema,
                             index,
                             commandStr,
                             "",
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

        model.addAttribute
                ("schemas", genericDAO.allSchemas((String) session.getAttribute("user_key")));

        model.addAttribute("chosenSchema", schema);

        return "indexes";

    }

}