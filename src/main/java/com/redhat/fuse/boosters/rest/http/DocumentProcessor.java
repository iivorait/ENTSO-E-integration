package com.redhat.fuse.boosters.rest.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import eu.entsoe.transparency.Period;
import eu.entsoe.transparency.Point;
import eu.entsoe.transparency.Publication_MarketDocument;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Get the currently valid price from the MarketDocument
 * @author 001320702
 *
 */
public class DocumentProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Publication_MarketDocument document = exchange.getIn().getBody(Publication_MarketDocument.class);
		String currentTime = exchange.getIn().getHeader("currentTime", String.class);
		Period period = document.getTimeSeries().get(0).getPeriod();
		/**
		 * The TimeSeries periods are from 22 to 22 of the next day (not from 0 to 24), it also changes with daylight saving (23 to 23)
		 * The Points in a Period look like this ("position 1" is the index 0)
			<Point>
					<position>1</position>
					<price.amount>10.37</price.amount>
			</Point>
			<Point>
					<position>2</position>
					<price.amount>11.67</price.amount>
			</Point>
			...
		 */

		//Calculate the index (starting from 0) of the Point that is currently valid
		int resolution = Integer.parseInt(period.getResolution(), 2, 4, 10); //60 or 15 minutes
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmX", Locale.ENGLISH);
		Date currentDate = sdf.parse(currentTime);
		Date startDate = sdf.parse(period.getTimeInterval().getStart());
		long diffInMillies = Math.abs(startDate.getTime() - currentDate.getTime());
		long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		int timeIndex = Math.floorDiv((int) diff, resolution);
		if(timeIndex == 24) {
			timeIndex = 0;
		}

		//sequential equal prices won't get their own points - there is a gap in the position value
                Iterator<Point> points = document.getTimeSeries().get(0).getPeriod().getPoint().iterator();
                ArrayList<Point> noGapsInPoints = new ArrayList<>();
                int position = 1;
                Point previousPoint = null;
                while(points.hasNext()) {
                    Point point = points.next();
                    while(position < point.getPosition()) {
                        position++;
                        noGapsInPoints.add(previousPoint);
                    }
                    previousPoint = point;
                }
                noGapsInPoints.add(previousPoint);
                
		Point valid = noGapsInPoints.get(timeIndex);
		exchange.getIn().setBody(valid.getPriceAmount());
	}

}
