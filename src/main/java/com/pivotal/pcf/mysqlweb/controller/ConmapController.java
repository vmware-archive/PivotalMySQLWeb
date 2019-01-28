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

import com.pivotal.pcf.mysqlweb.utils.ConnectionManager;
import com.pivotal.pcf.mysqlweb.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class ConmapController
{

    @GetMapping(value = "/viewconmap")
    public String viewConnections
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            log.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        log.info("Received request to show connection map");

        ConnectionManager cm = ConnectionManager.getInstance();

        String conMapAction = request.getParameter("conMapAction");
        String key = request.getParameter("key");

        if (conMapAction != null)
        {
            log.info("conMapAction = " + conMapAction);
            log.info("key = " + key);

            if (conMapAction.equalsIgnoreCase("DELETE"))
            {
                // remove this connection from Map and close it.
                cm.removeDataSource(key);
                log.info("Connection closed for key " + key);
                model.addAttribute("saved", "Successfully closed connection with key " + key);
            }
        }

        model.addAttribute("conmap", cm.getConnectionMap());
        model.addAttribute("conmapsize", cm.getConnectionListSize());

        return "conmap";
    }
}
