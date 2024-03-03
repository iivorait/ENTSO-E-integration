package com.redhat.fuse.boosters.rest.http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.entsoe.transparency.Publication_MarketDocument;

/**
 * Fetch the current energy price from the ENTSO-E API
 * https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html
 */
@Component
public class CamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "ENTSO-E Energy REST API integration")
                .apiProperty("api.version", "1.1")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .contextPath("/")
            ;

		onException(Exception.class)
			.handled(true)
			.log("Serving from cache - Error caught: ${exception.message}")
			.to("direct:checkCache")
			.setBody(simple("${property.responseXML}"))
			.choice()
	    		.when(simple("${property.list} == 'list'"))
	    			.to("direct:parsePriceList")
	    		.otherwise()
	    			.to("direct:parseCurrentPrice")
	    	.endChoice()
			;
        
        //4.2.10. Day Ahead Prices [12.1.D]
        rest("/dap").description("Day Ahead Prices [12.1.D]")
        	.get("{areacode}")
        		.description("Current price for area {areacode}")
        		.outType(String.class)
        		.param()
        			.name("areacode")
        			.description("The code from A.10. Areas list: https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_areas")
        			.example("10YFI-1--------U")
        			.type(RestParamType.path)
        		.endParam()
        		.responseMessage()
        			.code(200)
        			.message("The current price")
        			.example("200", "160.02")
        		.endResponseMessage()
        		.to("direct:callAPI")
        	.get("{areacode}/list")
        		.description("List of the daily prices for area {areacode}")
        		.outType(String.class)
        		.param()
        			.name("areacode")
        			.description("The code from A.10. Areas list: https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_areas")
        			.example("10YFI-1--------U")
        			.type(RestParamType.path)
        		.endParam()
        		.responseMessage()
        			.code(200)
        			.message("List of prices")
        			.example("200", "{\"mRID\":\"a26ccd...\",\"start\":\"2022-10-07T22:00Z\",\"end\":\"2022-10-08T22:00Z\",\"resolution\":\"PT60M\",\"currency\":\"EUR\",\"measureUnit\":\"MWH\",\"prices\":[{\"startUTC\":\"2022-10-07T22:00:00\",\"startLocal\":\"2022-10-08T01:00:00+0300\",\"price\":0.01},]}")
        		.endResponseMessage()
        		.to("direct:callListAPI")
        		;

	    from("direct:callListAPI")
	    	.routeId("callListAPI")
	    	.setProperty("list", constant("list")) //to be used in choice later
	    	.to("direct:callAPI")
	    ;
        
		JAXBContext con = JAXBContext.newInstance(Publication_MarketDocument.class);
	    JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(con);
	    from("direct:callAPI")
	    	.routeId("callAPI")
	    	.removeHeader(Exchange.HTTP_URI) //These headers are shared with the REST component and will interfere with HTTP4
	    	.removeHeader(Exchange.HTTP_PATH)
	    	.removeHeader(Exchange.HTTP_QUERY)

			.to("direct:checkCache")
			.process(exchange -> { //now+24h does not work for some reason
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				LocalDate tomorrow = LocalDate.now().plusDays(1);
				exchange.setProperty("tomorrowDate", tomorrow.format(formatter));
				//LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
				//exchange.setProperty("dayAfterTomorrowDate", dayAfterTomorrow.format(formatter));
			})
			//TODO: cleanup ugly copy-paste code here, I'm tired now
			.choice()
	    		.when(simple("${property.fetchToday} == 'true'")) //cache empty
					.log("Fetching new prices for today")
					.toD("https4://{{entsoe.endpoint}}?securityToken={{entsoe.securityToken}}&documentType=A44&in_Domain=${headers.areacode}&out_Domain=${headers.areacode}&periodStart=${date-with-timezone:now:UTC:yyyyMMddHH}00&periodEnd=${property.tomorrowDate}1200")
					.setProperty("responseXML", simple("${bodyAs(String)}")) //Allow the body to be read multiple times
					.setBody(simple("${property.responseXML}"))
					//		    .log("Before ${body}") 
					.unmarshal(jaxbDataFormat)
					//	    	.log("After ${body}")
					.to("direct:saveCache")
		 		.when(simple("${property.fetchTomorrow} == 'true'")) //cache found for today
					.log("Fetching new prices for tomorrow")
					.toD("https4://{{entsoe.endpoint}}?securityToken={{entsoe.securityToken}}&documentType=A44&in_Domain=${headers.areacode}&out_Domain=${headers.areacode}&periodStart=${property.tomorrowDate}1200&periodEnd=${property.tomorrowDate}1200")
	    			.setProperty("responseXML", simple("${bodyAs(String)}")) //Allow the body to be read multiple times
					.setBody(simple("${property.responseXML}"))
					.unmarshal(jaxbDataFormat)
					.to("direct:saveCache")
					.to("direct:checkCache") //read todays prices into memory
				.otherwise() //cache found for today and tomorrow
					.log("Using cached prices")
			.end()
			.setBody(simple("${property.responseXML}"))
			.choice()
	    		.when(simple("${property.list} == 'list'"))
	    			.to("direct:parsePriceList")
	    		.otherwise()
	    			.to("direct:parseCurrentPrice")
	    	.endChoice()
	        ;  
	    
	    from("direct:parseCurrentPrice")
		    .setHeader("currentTime", simple("${date-with-timezone:now:UTC:yyyy-MM-dd'T'HH:mmZ}"))
		    .process(new DocumentProcessor())
		    .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.TEXT_PLAIN))
//		    .log("Result ${body}")
	        ;  
	    
	    from("direct:parsePriceList")
		    .process(new PriceListProcessor())
		    .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
	    	.marshal().json(JsonLibrary.Jackson)
	        ;  
	    
    }

}
