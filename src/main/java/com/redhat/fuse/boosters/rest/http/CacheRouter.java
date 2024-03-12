package com.redhat.fuse.boosters.rest.http;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.file.Files;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.redhat.fuse.boosters.rest.models.PriceList;


/**
 * Save and read prices from cache
 */
@Component
public class CacheRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

		from("direct:checkCache")
			.routeId("checkCache")
			.setProperty("fetchToday", simple("true"))
			//the hard coded hour and minute need to change to HHss when moving to 15min resolution here
			//and the property.lastPriceTime needs to add the resolution to the timestamp for the cache to work correctly
			.setProperty("currentTime", simple("${date-with-timezone:now:UTC:yyyyMMddHH}0000"))
			.process(exchange -> {

				String directoryPath = exchange.getContext().resolvePropertyPlaceholders("{{cache.directory}}");
				String filePrefix = exchange.getContext().resolvePropertyPlaceholders("{{cache.filename}}");
				Long currentTime = Long.parseLong(exchange.getProperty("currentTime", String.class));
				ArrayList<String> validCacheFiles = new ArrayList<>();
				File directory = new File(directoryPath);
		
				if (directory.exists() && directory.isDirectory()) {
					File[] files = directory.listFiles();
					Arrays.sort(files);
		
					if (files != null) {
						for (File file : files) {
							// Check if the file name matches the pattern "entsoe-cache{integer}.xml"
							if (file.getName().matches(filePrefix + "\\d+\\.xml")) {
								// Extract the integer part from the file name
								try {
									Long filename = Long.parseLong(file.getName().replaceAll("[^0-9]", ""));
									// Check if the filename meets the condition
									//System.out.println("Found file: " + file.getName() + " and current time is " + currentTime);
									if (filename >= currentTime) {
										validCacheFiles.add(file.getPath());
									} 
								} catch (NumberFormatException e) {
									System.out.println("Error parsing " + file.getName());
								}
							} 
						}
					} 

					if(validCacheFiles.size() > 0) {
						String fileContent = new String(Files.readAllBytes(Paths.get(validCacheFiles.get(0))), StandardCharsets.UTF_8);
						exchange.setProperty("responseXML", fileContent);
						exchange.setProperty("fetchToday", false);
					}
				} else {
					System.out.println("Directory does not exist or is not a directory.");
				}
			})
			;

		from("direct:saveCache")
			.process(new PriceListProcessor())
			.process(exchange -> {
				PriceList list = exchange.getIn().getBody(PriceList.class);
				exchange.setProperty("lastPriceTime", list.getLatestPriceTime());
			})
			.setBody(simple("${property.responseXML}"))
			.to("file:{{cache.directory}}?fileName={{cache.filename}}${property.lastPriceTime}.xml")
	        ;  
	    
    }

}