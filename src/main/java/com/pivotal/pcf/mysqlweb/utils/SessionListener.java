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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionListener implements HttpSessionListener
{
    private HttpSession session = null;

    public void sessionCreated(HttpSessionEvent event)
    {
        // no need to do anything here as connection may not have been established yet
        session  = event.getSession();
        log.info("Session created for id " + session.getId());
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
        session  = event.getSession();
        ConnectionManager cm = null;

        try
        {
            cm = ConnectionManager.getInstance();
            cm.removeDataSource(session.getId());
            log.info("Session destroyed for id " + session.getId());
        }
        catch (Exception e)
        {
            log.info("SesssionListener.sessionDestroyed was unable to obtain Connection", e);
        }
    }
}

