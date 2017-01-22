package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.utils.SessionListener;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSessionListener;

@Configuration
public class ApplicationSessionConfiguration
{
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener()
    {
        return new ServletListenerRegistrationBean<HttpSessionListener>(new SessionListener());
    }

}
