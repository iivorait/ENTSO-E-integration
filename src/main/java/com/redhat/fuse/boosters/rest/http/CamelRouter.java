package com.redhat.fuse.boosters.rest.http;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
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
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .contextPath("/")
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
        		;

	    from("direct:callAPI")
	    	.routeId("callAPI")
	    	.removeHeader(Exchange.HTTP_URI) //These headers are shared with the REST component and will interfere with HTTP4
	    	.removeHeader(Exchange.HTTP_PATH)
	    	.removeHeader(Exchange.HTTP_QUERY)
	    	.hystrix()
	    		.hystrixConfiguration()
	    			.executionTimeoutInMilliseconds(30000)
	    			.circuitBreakerRequestVolumeThreshold(10)
	    			.metricsRollingPercentileWindowInMilliseconds(60000)
	    			.circuitBreakerSleepWindowInMilliseconds(60000)
	    		.end()
	    		.toD("https4://{{entsoe.endpoint}}?securityToken={{entsoe.securityToken}}&documentType=A44&in_Domain=${headers.areacode}&out_Domain=${headers.areacode}&periodStart=${date-with-timezone:now:UTC:yyyyMMddHH}00&periodEnd=${date-with-timezone:now:UTC:yyyyMMddHH}00")
	    		.setProperty("responseXML", simple("${bodyAs(String)}")) //Allow the body to be read multiple times
		    	.setBody(simple("${property.responseXML}"))
//		    	.log("API response: ${body}")
		    	.to("direct:parseXML")
	    	.onFallback()
	    		.transform().constant("Error with ENTSO-E API")
	    	.end()
	        ;  
	    
	    
	    JAXBContext con = JAXBContext.newInstance(Publication_MarketDocument.class);
	    JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(con);
	    from("direct:parseXML")
	    	.routeId("parseXML")
//		    .log("Before ${body}") 
	    	.unmarshal(jaxbDataFormat)
//	    	.log("After ${body}")
	    	.to("direct:parsedXML")
	    	;
	    
	    from("direct:parsedXML")
		    .setHeader("currentTime", simple("${date-with-timezone:now:UTC:HH}"))
		    .process(new DocumentProcessor())
		    .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.TEXT_PLAIN))
//		    .log("Result ${body}")
	        ;  
	    
    }

}