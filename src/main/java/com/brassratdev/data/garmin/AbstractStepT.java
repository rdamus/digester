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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractStep_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractStep_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StepId" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}StepId_t"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractStep_t", propOrder = {
    "stepId"
})
@XmlSeeAlso({
    RepeatT.class,
    StepT.class
})
public abstract class AbstractStepT {

    @XmlElement(name = "StepId")
    @XmlSchemaType(name = "positiveInteger")
    protected int stepId;

    /**
     * Gets the value of the stepId property.
     * 
     */
    public int getStepId() {
        return stepId;
    }

    /**
     * Sets the value of the stepId property.
     * 
     */
    public void setStepId(int value) {
        this.stepId = value;
    }

}
