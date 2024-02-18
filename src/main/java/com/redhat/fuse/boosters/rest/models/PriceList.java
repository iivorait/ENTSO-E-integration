package com.redhat.fuse.boosters.rest.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceList implements Serializable {

	private static final long serialVersionUID = -4665575197137935816L;
	
	public String mRID;
	public String start;
	public String end;
	public String resolution;
	public String currency;
	public String measureUnit;
	public float medianPrice;
	public ArrayList<PricePoint> prices;
	
	public String getmRID() {
		return mRID;
	}
	public void setmRID(String mRID) {
		this.mRID = mRID;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMeasureUnit() {
		return measureUnit;
	}
	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}
	public float getMedianPrice() {
		return medianPrice;
	}
	public void setMedianPrice(float medianPrice) {
		this.medianPrice = medianPrice;
	}
	public ArrayList<PricePoint> getPrices() {
		return prices;
	}
	public void setPrices(ArrayList<PricePoint> prices) {
		this.prices = prices;
	}
	
	public void calculateMedianPrice() {
		List<Float> values = new ArrayList<>();
        
        for (PricePoint obj : this.getPrices()) {
            values.add(obj.getPrice());
        }
        
        Collections.sort(values);
        
        int size = values.size();
        if (size % 2 == 0) {
            // If even number of elements, average the middle two
            this.medianPrice = (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0f;
        } else {
            // If odd number of elements, return the middle one
            this.medianPrice =  values.get(size / 2);
        }
	}

	public String getLatestPriceTime() {
		PricePoint lastPrice = this.prices.get(this.prices.size() - 1);
		return lastPrice.getStartUTC().replaceAll("[^0-9]", "");
	}
}
