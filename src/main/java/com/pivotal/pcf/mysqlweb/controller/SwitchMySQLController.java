package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.beans.Login;
import com.pivotal.pcf.mysqlweb.beans.MySQLInstance;
import com.pivotal.pcf.mysqlweb.beans.UserPref;
import com.pivotal.pcf.mysqlweb.beans.WebResult;
import com.pivotal.pcf.mysqlweb.dao.PivotalMySQLWebDAOFactory;
import com.pivotal.pcf.mysqlweb.dao.generic.Constants;
import com.pivotal.pcf.mysqlweb.dao.generic.GenericDAO;
import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.MysqlConnection;
import com.pivotal.pcf.mysqlweb.utils.Themes;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SwitchMySQLController {

    @Autowired
    UserPref userPref;

    @GetMapping(value = "/switchinstance")
    public String switchInstance
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        log.info("Received request to switch MySQL Instance");
        List mysqlServices = null;
        Map cfMySQLMap     = null;
        Login login = new Login();
        Map credentailsMap  = null;
        ConnectionManager cm = ConnectionManager.getInstance();
        WebResult databaseList;

        // get vcap_services
        String jsonString = System.getenv().get("VCAP_SERVICES");

        String instanceType = request.getParameter("instanceType");
        String instanceName = request.getParameter("instanceName");

        log.info("instanceType = " + instanceType);
        log.info("instanceName = " + instanceName);

        try {
            JsonParser parser = JsonParserFactory.getJsonParser();
            Map<String, Object> jsonMap = parser.parseMap(jsonString);

            mysqlServices = (List) jsonMap.get(instanceType);

            if (mysqlServices != null) {
                log.info(instanceType + " services size = " + mysqlServices.size());
                for (Object entry : mysqlServices) {
                    cfMySQLMap = (Map) entry;
                    if (cfMySQLMap.get("name").equals(instanceName)) {
                        credentailsMap = (Map) cfMySQLMap.get("credentials");
                        // depending on type retrieve credentials correctly
                        if (instanceType.equals("p-mysql")) {
                            log.info("Obtaining VCAP_SERVICES credentials - p-mysql");
                            login.setUrl((String) credentailsMap.get("jdbcUrl") + "&connectTimeout=1800000&socketTimeout=1800000&autoReconnect=true&reconnect=true");
                            login.setUsername((String) credentailsMap.get("username"));
                            login.setPassword((String) credentailsMap.get("password"));
                            login.setSchema((String) credentailsMap.get("name"));
                        } else if (instanceType.equals("p.mysql")) {
                            log.info("Obtaining VCAP_SERVICES credentials - p.mysql");
                            login.setUrl((String) credentailsMap.get("jdbcUrl"));
                            login.setUsername((String) credentailsMap.get("username"));
                            login.setPassword((String) credentailsMap.get("password"));
                            login.setSchema((String) credentailsMap.get("name"));
                        } else if (instanceType.equals("google-cloudsql-mysql")) {
                            log.info("Obtaining VCAP_SERVICES credentials - google-cloudsql-mysql");
                            login.setUrl("jdbc:mysql://" + (String) credentailsMap.get("host") + ":3306/" + (String) credentailsMap.get("database_name"));
                            login.setUsername((String) credentailsMap.get("Username"));
                            login.setPassword((String) credentailsMap.get("Password"));
                            login.setSchema((String) credentailsMap.get("database_name"));
                        } else if (instanceType.equals("cleardb")) {
                            log.info("Obtaining VCAP_SERVICES credentials - cleardb");
                            login.setUrl((String) credentailsMap.get("jdbcUrl") + "&connectTimeout=1800000&socketTimeout=1800000&autoReconnect=true&reconnect=true");
                            login.setUsername((String) credentailsMap.get("username"));
                            login.setPassword((String) credentailsMap.get("password"));
                            login.setSchema((String) credentailsMap.get("name"));
                        } else if (instanceType.equals("mariadbent")) {
                            log.info("Obtaining VCAP_SERVICES credentials - mariadbent");
                            login.setUrl((String) credentailsMap.get("jdbcUrl") + "&connectTimeout=1800000&socketTimeout=1800000&autoReconnect=true&reconnect=true");
                            login.setUsername((String) credentailsMap.get("username"));
                            login.setPassword((String) credentailsMap.get("password"));
                            login.setSchema((String) credentailsMap.get("name"));
                        } else {
                            // should never come here
                            log.info("Unknown MySQL instance type : " + instanceType);
                        }

                        break;
                    }

                }
            }

            log.info("Removing CF DataSource");

            cm.removeCfDataSource();

            log.info("Login : " + login);

            MysqlConnection newConn =
                    new MysqlConnection
                            (login.getUrl(), new java.util.Date().toString(), login.getUsername().toUpperCase());

            cm.addConnection(newConn, session.getId());

            cm.setupCFDataSource(login);

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

            log.info("schemaMap=" + schemaMap);
            session.setAttribute("schemaMap", schemaMap);

            List<MySQLInstance> services = Utils.getAllServices(jsonString);
            session.setAttribute("autobound", login.getSchema());
            session.setAttribute("servicesListSize", services.size());
            session.setAttribute("servicesList", services);
            model.addAttribute("databaseList", databaseList);

            return "main";
        }
        catch (Exception ex)
        {
            // we tried if we can't auto login , just present login screen
            model.addAttribute("loginObj", new Login("", "", "jdbc:mysql://localhost:3306/apples", "apples"));
            log.info("Auto Login via VCAP_SERVICES Failed - " + ex.getMessage());
        }

        session.setAttribute("themeMain", Themes.defaultTheme);
        session.setAttribute("themeMin", Themes.defaultThemeMin);
        return "login";

    }
}
