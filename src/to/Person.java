package to;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Navid on 11/17/2015.
 */
@XmlRootElement(name = "person")
@XmlType(propOrder = { "firstname", "lastname", "birthdate", "measureType" })
// XmlAccessorType indicates what to use to create the mapping: either FIELDS, PROPERTIES (i.e., getters/setters), PUBLIC_MEMBER or NONE (which means, you should indicate manually)
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {
    @XmlTransient
    private Long idPerson;
    private String firstname;
    private String lastname;
    private Date birthdate;
    @XmlElementWrapper(name="healthProfile")
    @XmlElement
    private List<HealthProfile> measureType = new ArrayList<HealthProfile>();

    public Person(){}

    //Deep Copy method for Person
    public Person(Person person_to_be_copied)
    {
        this.idPerson = person_to_be_copied.idPerson;
        this.firstname = person_to_be_copied.firstname;
        this.lastname = person_to_be_copied.lastname;
        this.birthdate = person_to_be_copied.birthdate;
        this.measureType = new ArrayList<HealthProfile>(person_to_be_copied.getMeasureType().size());
        for (HealthProfile healthProfile : person_to_be_copied.getMeasureType()) {
            this.measureType.add(new HealthProfile(healthProfile));
        }
    }

    //getters
    public Long getIdPerson(){
        return idPerson;
    }
    public String getFirstname(){
        return firstname;
    }
    public String getLastname(){return lastname;}
    public Date getBirthdate(){return birthdate;}
    public List<HealthProfile> getMeasureType () {return measureType;}

    // setters
    public void setIdPerson(Long idPerson){this.idPerson = idPerson;}
    public void setFirstname(String firstname){this.firstname = firstname;}
    public void setLastname(String lastname){this.lastname = lastname;}
    public void setBirthdate(Date birthdate){this.birthdate = birthdate;}
    public void setMeasure(List<HealthProfile> measureType) {this.measureType = measureType;}

    //Printer
    public void print()
    {
        System.out.println(this.getIdPerson());
        System.out.println(this.getFirstname());
        System.out.println(this.getLastname());
        System.out.println(this.getBirthdate());
        for(HealthProfile hp:this.getMeasureType())
        {
            System.out.println(hp.getMeasure() +" : "+ hp.getValue());
        }

    }
}
