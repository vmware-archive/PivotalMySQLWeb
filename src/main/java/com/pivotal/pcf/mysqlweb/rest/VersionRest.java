package com.pivotal.pcf.mysqlweb.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionRest
{
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public String sampleOuput()
    {
        return "Pivotal MySQLWeb version 1.0";
    }
}
