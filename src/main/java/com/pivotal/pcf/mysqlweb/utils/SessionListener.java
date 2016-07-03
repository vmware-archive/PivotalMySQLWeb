package com.pivotal.pcf.mysqlweb.utils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener
{
    protected static Logger logger = Logger.getLogger("controller");
    private HttpSession session = null;

    public void sessionCreated(HttpSessionEvent event)
    {
        // no need to do anything here as connection may not have been established yet
        session  = event.getSession();
        logger.info("Session created for id " + session.getId());
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
        session  = event.getSession();
	    /*
	     * Need to ensure Connection is closed from ConnectionManager
	     */

        ConnectionManager cm = null;

        try
        {
            cm = ConnectionManager.getInstance();
            cm.removeConnection(session.getId());
            logger.info("Session destroyed for id " + session.getId());
        }
        catch (Exception e)
        {
            logger.info("SesssionListener.sessionDestroyed Unable to obtain Connection", e);
        }
    }
}

