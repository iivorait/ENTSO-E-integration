package eu.entsoe.transparency;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Publication_MarketDocument", namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:3")
@XmlAccessorType(XmlAccessType.FIELD)
public class Publication_MarketDocument implements Serializable {

	private static final long serialVersionUID = 8150816647100149588L;

	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:3" )
	private String mRID;
	@XmlElement
	private String type;
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:3" )
	private ArrayList<TimeSeries> TimeSeries;
	
	public String getmRID() {
		return mRID;
	}
	public void setmRID(String mRID) {
		this.mRID = mRID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<TimeSeries> getTimeSeries() {
		return TimeSeries;
	}
	public void setTimeSeries(ArrayList<TimeSeries> timeSeries) {
		TimeSeries = timeSeries;
	}
}
