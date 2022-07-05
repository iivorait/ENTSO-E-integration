package com.redhat.fuse.boosters.rest.http;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAPI() throws Exception {
    	String result = this.restTemplate.getForObject("http://localhost:" + port + "/camel/dap/10YFI-1--------U", String.class);
    	float resultNum = Float.parseFloat(result);
        Assert.assertTrue(resultNum > 0);
        Assert.assertTrue(resultNum < 1000000);
        System.out.println("TESTED PRICE: " + resultNum);
    }

    @Test
    public void healthShouldReturnOkMessage() throws Exception {
        Assert.assertEquals( "{\"status\":\"UP\"}", this.restTemplate.getForObject("http://localhost:" + port + "/actuator/health", String.class));
    }
}
