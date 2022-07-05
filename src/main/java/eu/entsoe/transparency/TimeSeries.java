package eu.entsoe.transparency;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimeSeries implements Serializable {

	private static final long serialVersionUID = -7377037043746052761L;

	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private String mRID;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private Period Period;

	public String getmRID() {
		return mRID;
	}

	public void setmRID(String mRID) {
		this.mRID = mRID;
	}

	public Period getPeriod() {
		return Period;
	}

	public void setPeriod(Period period) {
		Period = period;
	}
}
