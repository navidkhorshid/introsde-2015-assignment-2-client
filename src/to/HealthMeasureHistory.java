package to;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created by Navid on 11/17/2015.
 */
@XmlRootElement(name="measure")
@XmlType(propOrder = { "mid","value","created"})
// XmlAccessorType indicates what to use to create the mapping: either FIELDS, PROPERTIES (i.e., getters/setters), PUBLIC_MEMBER or NONE (which means, you should indicate manually)
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthMeasureHistory {
    private int mid;
    private Date created;
    private String value;
    @XmlTransient
    private MeasureDefinition measureDefinition;
    @XmlTransient
    private Person person;

    public HealthMeasureHistory() {}

    public HealthMeasureHistory(HealthMeasureHistory healthMeasureHistory)
    {
        this.mid = healthMeasureHistory.mid;
        this.created = healthMeasureHistory.created;
        this.value = healthMeasureHistory.value;
        this.measureDefinition = new MeasureDefinition(healthMeasureHistory.getMeasureDefinition());
        this.person = new Person(healthMeasureHistory.getPerson());
    }

    //getters
    public int getMid() {return this.mid;}
    public Date getCreated() {return this.created;}
    public String getValue() {return this.value;}
    public MeasureDefinition getMeasureDefinition() {return measureDefinition;}
    public Person getPerson() {
        return person;
    }

    //setters
    public void setMid(int idMeasureHistory) {this.mid = idMeasureHistory;}
    public void setCreated(Date created) {this.created = created;}
    public void setValue(String value) {this.value = value;}
    public void setMeasureDefinition(MeasureDefinition measureDefinition) {this.measureDefinition = measureDefinition;}
    public void setPerson(Person person) {
        this.person = person;
    }
}
