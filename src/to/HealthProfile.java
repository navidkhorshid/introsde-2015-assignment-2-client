package to;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Navid on 11/17/2015.
 */
@XmlRootElement(name="healthProfile")
@XmlType(propOrder = { "measure", "value" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthProfile {
    //@XmlTransient
    //private int mid;
    private String measure;
    private String value;

    public HealthProfile() {}

    //FOR COPYING DATA in Person
    public HealthProfile(HealthProfile healthProfile)
    {
        this.measure = healthProfile.measure;
        this.value = healthProfile.value;
    }

    //getters
    //public int getMid() {return this.mid;}
    public String getValue() {return this.value;}
    public String getMeasure() {return measure;}

    //setters
    //public void setMid(int idMeasureHistory) {this.mid = idMeasureHistory;}
    public void setValue(String value) {this.value = value;}
    public void setMeasure(String measure) {this.measure = measure;}
}
