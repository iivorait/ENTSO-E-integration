package eu.entsoe.transparency;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Period implements Serializable {

	private static final long serialVersionUID = 6800802536378441800L;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private TimeInterval timeInterval;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private String resolution;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private ArrayList<Point> Point;

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public ArrayList<Point> getPoint() {
		return Point;
	}

	public void setPoint(ArrayList<Point> point) {
		Point = point;
	}
}
