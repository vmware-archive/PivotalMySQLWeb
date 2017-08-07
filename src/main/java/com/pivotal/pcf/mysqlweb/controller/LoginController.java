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

import com.pivotal.pcf.mysqlweb.beans.Login;
import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.Constants;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Controller;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController
{
    protected static Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    UserPref userPref;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login(Model model, HttpSession session) throws Exception
    {
        logger.info("Received request to show login page");
        WebResult databaseList;
        SingleConnectionDataSource ds = new SingleConnectionDataSource();

        String jsonString = null;
        jsonString = System.getenv().get("VCAP_SERVICES");

        if (jsonString != null)
        {
            if (jsonString.length() > 0)
            {
                try
                {
                    ConnectionManager cm = ConnectionManager.getInstance();

                    logger.info("** Attempting login using VCAP_SERVICES **");
                    logger.info(jsonString);

                    Login login = Utils.parseLoginCredentials(jsonString);

                    logger.info("Login : " + login);

                    MysqlConnection newConn =
                            new MysqlConnection
                                    (login.getUrl(), new java.util.Date().toString(), login.getUsername().toUpperCase());

                    cm.addConnection(newConn, session.getId());

                    cm.setupCFDataSource(login);
                    /*
                    cm.addDataSourceConnection(AdminUtil.newSingleConnectionDataSource
                            (login.getUrl(), login.getUsername(), login.getPassword()), session.getId());
                    */
                    session.setAttribute("user_key", session.getId());
                    session.setAttribute("user", login.getUsername().toUpperCase());
                    session.setAttribute("schema", login.getSchema());
                    session.setAttribute("url", login.getUrl());
                    session.setAttribute("prefs", userPref);
                    session.setAttribute("history", new LinkedList());
                    session.setAttribute("connectedAt", new java.util.Date().toString());
                    session.setAttribute("themeMain", Themes.defaultTheme);
                    session.setAttribute("themeMin", Themes.defaultThemeMin);

                    GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

                    databaseList = genericDAO.runGenericQuery
                            (Constants.DATABASE_LIST, null, session.getId(), -1);

                    Map<String, Long> schemaMap;
                    schemaMap = genericDAO.populateSchemaMap(login.getSchema(), session.getId());

                    logger.info("schemaMap=" + schemaMap);
                    session.setAttribute("schemaMap", schemaMap);

                    logger.info("schemaMap=" + schemaMap);
                    logger.info(userPref.toString());

                    String autobound = mysqlInstanceType(jsonString);

                    session.setAttribute("autobound", autobound);
                    model.addAttribute("databaseList", databaseList);

                    return "main";

                }
                catch (Exception ex)
                {
                    // we tried if we can't auto login , just present login screen
                    model.addAttribute("loginObj", new Login("", "", "jdbc:mysql://localhost:3306/apples", "apples"));
                    logger.info("Auto Login via VCAP_SERVICES Failed - " + ex.getMessage());
                }

            }
        }
        else
        {
            model.addAttribute("loginObj", new Login("", "", "jdbc:mysql://localhost:3306/apples", "apples"));
        }

        session.setAttribute("themeMain", Themes.defaultTheme);
        session.setAttribute("themeMin", Themes.defaultThemeMin);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login
            (@RequestParam(value="username", required=true) String username,
             @RequestParam(value="password", required=true) String password,
             @RequestParam(value="url", required=true) String url,
             Model model,
             HttpSession session) throws Exception
    {
        WebResult databaseList, schemaMapResult;
        SingleConnectionDataSource ds = new SingleConnectionDataSource();

        logger.info("Received request to login");
        ConnectionManager cm = ConnectionManager.getInstance();

        Login loginObj = new Login(username, password, url, "");

        logger.info("url {" + loginObj.getUrl() + "}");
        logger.info("user {" + loginObj.getUsername() + "}");

        try
        {

            MysqlConnection newConn =
                    new MysqlConnection
                            (url,
                             new java.util.Date().toString(),
                             username.toUpperCase());

            cm.addConnection(newConn, session.getId());
            cm.addDataSourceConnection(AdminUtil.newSingleConnectionDataSource
                    (url, username, password), session.getId());

            String schema = url.substring(url.lastIndexOf("/") + 1);

            session.setAttribute("user_key", session.getId());
            session.setAttribute("user", username.toUpperCase());
            session.setAttribute("schema", schema);
            session.setAttribute("url", url);
            session.setAttribute("prefs", userPref);
            session.setAttribute("history", new LinkedList());
            session.setAttribute("connectedAt", new java.util.Date().toString());
            session.setAttribute("themeMain", Themes.defaultTheme);
            session.setAttribute("themeMin", Themes.defaultThemeMin);

            GenericDAO genericDAO = PivotalMySQLWebDAOFactory.getGenericDAO();

            databaseList = genericDAO.runGenericQuery
                    (Constants.DATABASE_LIST, null, session.getId(), -1);

            Map<String, Long> schemaMap;
            schemaMap = genericDAO.populateSchemaMap(schema, session.getId());

            logger.info("schemaMap=" + schemaMap);
            session.setAttribute("schemaMap", schemaMap);

            model.addAttribute("databaseList", databaseList);

            return "main";
        }
        catch (Exception ex)
        {
            model.addAttribute("loginerror", ex.getMessage());
            model.addAttribute("loginObj");
            session.setAttribute("themeMain", Themes.defaultTheme);
            session.setAttribute("themeMin", Themes.defaultThemeMin);
            return "login";
        }
    }

    private String mysqlInstanceType (String jsonString)
    {

        String mysqlType = null;

        JsonParser parser = JsonParserFactory.getJsonParser();

        Map<String, Object> jsonMap = parser.parseMap(jsonString);

        List mysqlService = (List) jsonMap.get("cleardb");

        if (mysqlService == null)
        {
            mysqlType = "P-MYSQL";
        }
        else
        {
            mysqlType = "CLEARDB";
        }


        return mysqlType;

    }

}
