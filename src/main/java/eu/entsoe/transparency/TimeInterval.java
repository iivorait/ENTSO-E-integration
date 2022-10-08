package eu.entsoe.transparency;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimeInterval implements Serializable {

	private static final long serialVersionUID = 6800802536378441880L;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private String start;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private String end;

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

}
