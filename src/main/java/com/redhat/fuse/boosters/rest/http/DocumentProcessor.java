package com.redhat.fuse.boosters.rest.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import eu.entsoe.transparency.Point;
import eu.entsoe.transparency.Publication_MarketDocument;

/**
 * Get the currently valid price from the MarketDocument
 * @author 001320702
 *
 */
public class DocumentProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Publication_MarketDocument document = exchange.getIn().getBody(Publication_MarketDocument.class);
		int time = exchange.getIn().getHeader("currentTime", int.class);
		//The TimeSeries periods are from 22 to 22 of the next day (not from 0 to 24), 
		//so the index of the current time in the array is the current time + 2, except for the 22 and 23 (0 and 1 respectively) 
		int timeIndex;
		if(time == 22) {
			timeIndex = 0;
		} else if(time == 23) {
			timeIndex = 1;
		} else {
			timeIndex = time + 2;
		}
		Point valid = document.getTimeSeries().get(0).getPeriod().getPoint().get(timeIndex);
		exchange.getIn().setBody(valid.getPriceAmount());
	}

}
