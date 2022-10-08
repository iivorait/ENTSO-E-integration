package com.redhat.fuse.boosters.rest.models;

import java.io.Serializable;
import java.util.ArrayList;

public class PriceList implements Serializable {

	private static final long serialVersionUID = -4665575197137935816L;
	
	public String mRID;
	public String start;
	public String end;
	public String resolution;
	public String currency;
	public String measureUnit;
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
	public ArrayList<PricePoint> getPrices() {
		return prices;
	}
	public void setPrices(ArrayList<PricePoint> prices) {
		this.prices = prices;
	}
	
}
