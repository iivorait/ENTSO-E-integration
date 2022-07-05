package eu.entsoe.transparency;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Point implements Serializable {

	private static final long serialVersionUID = 8964515245746898666L;
	
	@XmlElement(namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private int position;
	
	@XmlElement(name="price.amount", namespace = "urn:iec62325.351:tc57wg16:451-3:publicationdocument:7:0" )
	private float priceAmount;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public float getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(float priceAmount) {
		this.priceAmount = priceAmount;
	}
}
