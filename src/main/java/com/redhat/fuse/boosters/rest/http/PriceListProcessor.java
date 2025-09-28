package com.redhat.fuse.boosters.rest.http;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.redhat.fuse.boosters.rest.models.PriceList;
import com.redhat.fuse.boosters.rest.models.PricePoint;

import eu.entsoe.transparency.Point;
import eu.entsoe.transparency.Publication_MarketDocument;

/**
 * Generate a price list from the MarketDocument
 * @author 001320702
 *
 */
public class PriceListProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Publication_MarketDocument document = exchange.getIn().getBody(Publication_MarketDocument.class);
		PriceList pricelist = new PriceList();

		pricelist.setmRID(document.getmRID());
		pricelist.setStart(document.getTimeSeries().get(0).getPeriod().getTimeInterval().getStart());
		pricelist.setEnd(document.getTimeSeries().get(0).getPeriod().getTimeInterval().getEnd());
		pricelist.setResolution(document.getTimeSeries().get(0).getPeriod().getResolution());
		pricelist.setCurrency(document.getTimeSeries().get(0).getCurrency());
		pricelist.setMeasureUnit(document.getTimeSeries().get(0).getMeasureUnit());

		Date start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmX", Locale.ENGLISH).parse(pricelist.getStart());
		Duration resolution = Duration.parse(pricelist.getResolution());
		ZonedDateTime startLocal = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault());
		ZonedDateTime startUTC = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.of("Z"));
		ArrayList<PricePoint> prices = new ArrayList<>();
		Iterator<Point> points = document.getTimeSeries().get(0).getPeriod().getPoint().iterator();

		// sequential equal prices won't get their own points - there is a gap in the position value
		ArrayList<Point> noGapsInPoints = new ArrayList<>();
		int position = 1;
		Point previousPoint = null;
		while (points.hasNext()) {
			Point point = points.next();
			while (position < point.getPosition()) {
				position++;
				noGapsInPoints.add(previousPoint);
			}
			previousPoint = point;
		}
		noGapsInPoints.add(previousPoint);

		for (Point point : noGapsInPoints) {
			PricePoint price = new PricePoint();
			price.setPrice(point.getPriceAmount());
			price.setStartLocal(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZ").format(startLocal));
			price.setStartUTC(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(startUTC));
			startLocal = startLocal.plus(resolution);
			startUTC = startUTC.plus(resolution);
			prices.add(price);
		}
		pricelist.setPrices(prices);
		pricelist.calculateMedianPrice();

		exchange.getIn().setBody(pricelist);
	}

}
