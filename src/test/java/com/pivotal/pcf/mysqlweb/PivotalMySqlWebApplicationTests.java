package com.pivotal.pcf.mysqlweb;

import com.pivotal.pcf.mysqlweb.rest.VersionRest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PivotalMySqlWebApplication.class)
@WebAppConfiguration
public class PivotalMySqlWebApplicationTests {

	@Test
	public void contextLoads() {
	}

}
