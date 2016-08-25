package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.Login;
import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.MysqlConnection;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Controller;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController
{
    protected static Logger logger = Logger.getLogger("controller");

    @Autowired
    UserPref userPref;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login(Model model, HttpSession session) throws Exception
    {
        logger.info("Received request to show login page");

        String jsonString = null;
        jsonString = System.getenv().get("VCAP_SERVICES");

        if (jsonString != null)
        {
            if (jsonString.length() > 0)
            {
                try
                {
                    ConnectionManager cm = ConnectionManager.getInstance();
                    Connection conn;

                    logger.info("** Attempting login using VCAP_SERVICES **");
                    logger.info(jsonString);

                    Login login = parseLoginCredentials(jsonString);

                    logger.info("Login : " + login);

                    conn = AdminUtil.getNewConnection
                            (login.getUrl(), login.getUsername(), login.getPassword());

                    conn.setAutoCommit(true);

                    MysqlConnection newConn =
                            new MysqlConnection
                                    (conn, login.getUrl(), new java.util.Date().toString(), login.getUsername().toUpperCase());

                    cm.addConnection(newConn, session.getId());

                    session.setAttribute("user_key", session.getId());
                    session.setAttribute("user", login.getUsername().toUpperCase());
                    session.setAttribute("schema", login.getSchema());
                    session.setAttribute("url", login.getUrl());
                    session.setAttribute("prefs", userPref);
                    session.setAttribute("history", new LinkedList());
                    session.setAttribute("connectedAt", new java.util.Date().toString());

                    Map<String, String> schemaMap = AdminUtil.getSchemaMap();

                    schemaMap = QueryUtil.populateSchemaMap
                            (conn, schemaMap, login.getSchema());

                    session.setAttribute("schemaMap", schemaMap);

                    logger.info("schemaMap=" + schemaMap);
                    logger.info(userPref.toString());

                    String autobound = mysqlInstanceType(jsonString);

                    session.setAttribute("autobound", autobound);

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
        logger.info("Received request to login");
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn;

        Login loginObj = new Login(username, password, url, "");

        logger.info("url {" + loginObj.getUrl() + "}");
        logger.info("user {" + loginObj.getUsername() + "}");

        try
        {
            conn = AdminUtil.getNewConnection
                    (url, username, password);

            conn.setAutoCommit(true);

            MysqlConnection newConn =
                    new MysqlConnection
                            (conn,
                                    url,
                                    new java.util.Date().toString(),
                                    username.toUpperCase());

            cm.addConnection(newConn, session.getId());

            String schema = url.substring(url.lastIndexOf("/") + 1);

            session.setAttribute("user_key", session.getId());
            session.setAttribute("user", username.toUpperCase());
            session.setAttribute("schema", schema);
            session.setAttribute("url", url);
            session.setAttribute("prefs", userPref);
            session.setAttribute("history", new LinkedList());
            session.setAttribute("connectedAt", new java.util.Date().toString());

            Map<String, String> schemaMap = AdminUtil.getSchemaMap();

            schemaMap = QueryUtil.populateSchemaMap
                    (conn, schemaMap, schema);

            session.setAttribute("schemaMap", schemaMap);

            logger.info("schemaMap=" + schemaMap);
            logger.info(userPref.toString());

            return "main";
        }
        catch (Exception ex)
        {
            model.addAttribute("loginerror", ex.getMessage());
            model.addAttribute("loginObj");
            return "login";
        }
    }

    private Login parseLoginCredentials (String jsonString)
    {
        Login login = new Login();
        JsonParser parser = JsonParserFactory.getJsonParser();

        Map<String, Object> jsonMap = parser.parseMap(jsonString);

        List mysqlService = (List) jsonMap.get("cleardb");

        if (mysqlService == null)
        {
            // just check if it's "p-mysql"
            mysqlService = (List) jsonMap.get("p-mysql");
        }

        
        if (mysqlService != null)
        {
            logger.info("Obtaining VCAP_SERVICES credentials");
            Map clearDBMap = (Map) mysqlService.get(0);
            Map credentailsMap = (Map) clearDBMap.get("credentials");

            login.setUrl((String) credentailsMap.get("jdbcUrl") + "&connectTimeout=1800000&socketTimeout=1800000&autoReconnect=true");
            login.setUsername((String) credentailsMap.get("username"));
            login.setPassword((String) credentailsMap.get("password"));
            login.setSchema((String) credentailsMap.get("name"));
        }

        return login;

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
