//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.04 at 02:42:31 PM PST 
//


package com.brassratdev.data.garmin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomHeartRateZone_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomHeartRateZone_t">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}Zone_t">
 *       &lt;sequence>
 *         &lt;element name="Low" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}HeartRateValue_t"/>
 *         &lt;element name="High" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}HeartRateValue_t"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomHeartRateZone_t", propOrder = {
    "low",
    "high"
})
public class CustomHeartRateZoneT
    extends ZoneT
{

    @XmlElement(name = "Low", required = true)
    protected HeartRateValueT low;
    @XmlElement(name = "High", required = true)
    protected HeartRateValueT high;

    /**
     * Gets the value of the low property.
     * 
     * @return
     *     possible object is
     *     {@link HeartRateValueT }
     *     
     */
    public HeartRateValueT getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeartRateValueT }
     *     
     */
    public void setLow(HeartRateValueT value) {
        this.low = value;
    }

    /**
     * Gets the value of the high property.
     * 
     * @return
     *     possible object is
     *     {@link HeartRateValueT }
     *     
     */
    public HeartRateValueT getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeartRateValueT }
     *     
     */
    public void setHigh(HeartRateValueT value) {
        this.high = value;
    }

}
