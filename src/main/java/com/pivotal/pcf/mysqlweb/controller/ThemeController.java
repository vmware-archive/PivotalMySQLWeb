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

import com.pivotal.pcf.mysqlweb.utils.Themes;
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
public class ThemeController
{
    @GetMapping(value = "/selecttheme")
    public String alterTheme
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (Utils.verifyConnection(response, session))
        {
            log.info("user_key is null OR Connection stale so new Login required");
            return null;
        }

        log.info("Received request alter theme");

        String selectedTheme = request.getParameter("theme");

        if (selectedTheme != null)
        {
            if (selectedTheme.trim().length() != 0)
            {
                switch (selectedTheme) {
                    case "default":
                        session.setAttribute("themeMain", Themes.defaultTheme);
                        session.setAttribute("themeMin", Themes.defaultThemeMin);
                        break;
                    case "cyborg":
                        session.setAttribute("themeMain", Themes.defaultThemeCyborg);
                        session.setAttribute("themeMin", Themes.defaultThemeCyborgMin);
                        break;
                    case "sandstone":
                        session.setAttribute("themeMain", Themes.defaultThemeSandstone);
                        session.setAttribute("themeMin", Themes.defaultThemeSandstoneMin);
                        break;
                    case "slate":
                        session.setAttribute("themeMain", Themes.defaultThemeSlate);
                        session.setAttribute("themeMin", Themes.defaultThemeSlateMin);
                        break;
                    case "spacelab":
                        session.setAttribute("themeMain", Themes.defaultThemeSpacelab);
                        session.setAttribute("themeMin", Themes.defaultThemeSpacelabMin);
                        break;
                }

                session.setAttribute("theme", selectedTheme);
                log.info("New theme set as : " + selectedTheme);
            }

        }

        return "main";
    }
}
