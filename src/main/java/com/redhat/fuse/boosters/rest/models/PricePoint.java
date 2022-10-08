package com.redhat.fuse.boosters.rest.models;

import java.io.Serializable;

public class PricePoint implements Serializable {

	private static final long serialVersionUID = -4665575197137935817L;
	
	public String startUTC;
	public String startLocal;
	public float price;
	
	public String getStartUTC() {
		return startUTC;
	}
	public void setStartUTC(String startUTC) {
		this.startUTC = startUTC;
	}
	public String getStartLocal() {
		return startLocal;
	}
	public void setStartLocal(String startLocal) {
		this.startLocal = startLocal;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

}
