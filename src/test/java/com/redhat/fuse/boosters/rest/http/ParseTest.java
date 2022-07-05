package com.redhat.fuse.boosters.rest.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParseTest extends CamelTestSupport {

	@Produce(uri = "direct:parseXML") //viestien injektointia varten
	private ProducerTemplate template;

	@EndpointInject(uri = "mock:parsed") //tarkastelua varten
	private MockEndpoint mockParsed;

	@Override
	public boolean isUseAdviceWith() {  
		return true;  //Tärkeä, vaaditaan routen muokkausta varten
	}

	@After
	public void after() throws Exception {
		context.stop();
	}

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new CamelRouter();
	}

	@Before
	public void mockEndpoints() throws Exception {
		AdviceWithRouteBuilder mockRoute = new AdviceWithRouteBuilder() {

			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("direct:parsedXML").skipSendToOriginalEndpoint().to("mock:parsed");
			}
		};
		context.getRouteDefinition("parseXML").adviceWith(context, mockRoute);
		context.start();
	}

	@Test
	public void testRouterr() throws Exception {
		NotifyBuilder builder = new NotifyBuilder(context).whenDone(1).create();
		builder.matches(2, TimeUnit.SECONDS);
		String xml = this.readFile("src/test/resources/test.xml");
//		System.out.println(xml);
		template.sendBody(xml);
		mockParsed.expectedMessageCount(1);
		Exchange viesti = mockParsed.getExchanges().get(0);
		System.out.println("KOE3: " + viesti.getIn().getBody(String.class));
		assertMockEndpointsSatisfied();
	}

	private String readFile(String filepath) {
		StringBuilder sb = new StringBuilder();
		try {
			File myObj = new File(filepath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				//		        System.out.println(data);
				sb.append(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return sb.toString();
	}
}
