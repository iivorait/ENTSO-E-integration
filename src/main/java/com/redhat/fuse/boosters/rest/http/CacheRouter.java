package com.redhat.fuse.boosters.rest.http;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


/**
 * Save a successful result to cache and serve it if an error occurs
 */
@Component
public class CacheRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

	    from("direct:serveFromCache")
	    	.routeId("serveFromCache")
	    	.log("Serving from cache")
			.process(exchange -> {
				try {
					String directory = exchange.getContext().resolvePropertyPlaceholders("{{cache.directory}}");
					String filename = exchange.getContext().resolvePropertyPlaceholders("{{cache.filename}}");
					String fileContent = new String(Files.readAllBytes(Paths.get(directory + filename)), StandardCharsets.UTF_8);
					exchange.setProperty("responseXML", fileContent);
					exchange.setProperty("cacheFail", false);
				} catch (Exception e) {
					exchange.setProperty("cacheFail", true);
				}
			})
			.choice()
				.when(simple("${property.cacheFail} == true"))
					.log("Cache read error")
					.to("direct:errorMessage")
				.otherwise()
					.setBody(simple("${property.responseXML}"))
					.to("direct:parseXML")
	    	;
        
		from("direct:errorMessage")
			.transform().simple("${property.errorMessage}")
	        ; 

		from("direct:saveCache")
			.setBody(simple("${property.responseXML}"))
			.to("file:{{cache.directory}}?fileName={{cache.filename}}")
	        ;  
	    
    }

}