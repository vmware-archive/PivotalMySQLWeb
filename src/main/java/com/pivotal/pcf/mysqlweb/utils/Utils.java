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
package com.pivotal.pcf.mysqlweb.utils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivotal.pcf.mysqlweb.beans.Login;
import org.apache.log4j.Logger;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Utils
{
    protected static Logger logger = Logger.getLogger(Utils.class);

    public static String getVcapServices ()
    {
        String jsonString = null;
        jsonString = System.getenv().get("VCAP_SERVICES");
        return jsonString;
    }

    public static String applicationIndex ()
    {
        String instanceIndex = "N/A";

        try
        {
            instanceIndex = getVcapApplicationMap().getOrDefault("instance_index", "N/A").toString();
        }
        catch (Exception ex)
        {

        }

        return instanceIndex;
    }

    static private Map getVcapApplicationMap() throws Exception {
        return getEnvMap("VCAP_APPLICATION");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getEnvMap(String vcap) throws Exception {
        String vcapEnv = System.getenv(vcap);
        ObjectMapper mapper = new ObjectMapper();

        if (vcapEnv != null) {
            Map<String, ?> vcapMap = mapper.readValue(vcapEnv, Map.class);
            return vcapMap;
        }

        return new HashMap<String, String>();
    }

    public static Map<String, String> jvmPropertyMap ()
    {
        Properties props = System.getProperties();
        Map<String, String> map = new HashMap<String, String>((Map) props);

        return map;
    }

    public static boolean verifyConnection(HttpServletResponse response, HttpSession session) throws Exception {
        if (session.getAttribute("user_key") == null)
        {
            response.sendRedirect("/");
            return true;
        }
        else
        {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null )
            {
                response.sendRedirect("/");
                return true;
            }
            else
            {
                if (conn.isClosed() || ! conn.isValid(5))
                {
                    logger.info("Connection = null OR Connection no longer valid");
                    // Need logic to reconnect here if VCAP_SERVICES is populated and running in CF
                    ConnectionManager cm = ConnectionManager.getInstance();

                    String jsonString = System.getenv().get("VCAP_SERVICES");
                    if (jsonString != null) {
                        if (jsonString.length() > 0) {
                            logger.info("** Attempting login using VCAP_SERVICES **");

                            Login login = Utils.parseLoginCredentials(jsonString);
                            cm.removeConnection((String) session.getAttribute("user_key"));

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

                            return false;
                        }
                    }

                    response.sendRedirect("/");
                    return true;
                }
            }

        }
        return false;
    }

    public static Login parseLoginCredentials (String jsonString)
    {
        Login login = new Login();
        JsonParser parser = JsonParserFactory.getJsonParser();

        Map<String, Object> jsonMap = parser.parseMap(jsonString);

        List mysqlService = (List) jsonMap.get("cleardb");

        if (mysqlService == null)
        {
            // just check if it's "p-mysql"
            mysqlService = (List) jsonMap.get("p-mysql");

            // for dedicated v2 mysql we
            if (mysqlService == null)
            {
                mysqlService = (List) jsonMap.get("p.mysql");
            }
        }


        if (mysqlService != null)
        {
            logger.info("Obtaining VCAP_SERVICES credentials");
            Map clearDBMap = (Map) mysqlService.get(0);
            Map credentailsMap = (Map) clearDBMap.get("credentials");

            login.setUrl((String) credentailsMap.get("jdbcUrl") + "&connectTimeout=1800000&socketTimeout=1800000&autoReconnect=true&reconnect=true");
            //login.setUrl((String) credentailsMap.get("jdbcUrl") + "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");

            login.setUsername((String) credentailsMap.get("username"));
            login.setPassword((String) credentailsMap.get("password"));
            login.setSchema((String) credentailsMap.get("name"));
        }

        return login;

    }
}
