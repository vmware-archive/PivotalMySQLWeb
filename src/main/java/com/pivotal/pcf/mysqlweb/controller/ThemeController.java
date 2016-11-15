package com.pivotal.pcf.mysqlweb.controller;

import com.pivotal.pcf.mysqlweb.utils.AdminUtil;
import com.pivotal.pcf.mysqlweb.utils.QueryUtil;
import com.pivotal.pcf.mysqlweb.utils.Themes;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.Map;

@Controller
public class ThemeController
{
    protected static Logger logger = Logger.getLogger("controller");

    @RequestMapping(value = "/selecttheme", method = RequestMethod.GET)
    public String alterTheme
            (Model model, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws Exception
    {
        if (session.getAttribute("user_key") == null)
        {
            logger.info("user_key is null new Login required");
            response.sendRedirect("/");
            return null;
        }
        else
        {
            Connection conn = AdminUtil.getConnection((String) session.getAttribute("user_key"));
            if (conn == null )
            {
                response.sendRedirect("/");
                return null;
            }
            else
            {
                if (conn.isClosed())
                {
                    response.sendRedirect("/");
                    return null;
                }
            }

        }

        logger.info("Received request alter theme");

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
                }

                session.setAttribute("theme", selectedTheme);
                logger.info("New theme set as : " + selectedTheme);
            }

        }

        return "main";
    }
}
