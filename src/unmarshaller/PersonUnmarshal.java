package unmarshaller;

import com.google.gson.*;
import to.People;
import to.Person;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created by Navid on 11/6/2015.
 */
public class PersonUnmarshal
{
    public PersonUnmarshal(){}

    public List<Person> getPersons(String data_xml)
    {
        People people = new People();
        try
        {
            JAXBContext jc = JAXBContext.newInstance(People.class);
            //System.out.println("Output from XML");
            Unmarshaller um = jc.createUnmarshaller();
            StringReader reader = new StringReader(data_xml);
            people = (People) um.unmarshal(reader);
        }catch (Exception e) {}
        return people.getPerson();
    }

    public List<Person> getPersonsJSON(String data_json)
    {
        People people = new People();
        try
        {
            Gson gson = new Gson();
            String jsonInString = data_json;
            people = gson.fromJson(jsonInString, People.class);

        }catch (Exception e) {}
        return people.getPerson();
    }

    public Person getPerson(String data_xml)
    {
        Person person = new Person();
        try
        {
            JAXBContext jc = JAXBContext.newInstance(Person.class);
            Unmarshaller um = jc.createUnmarshaller();
            StringReader reader = new StringReader(data_xml);
            person = (Person) um.unmarshal(reader);
        }catch (Exception e) {}
        return person;
    }

    public Person getPersonJSON(String data_json, boolean custom_date_flag)
    {
        //System.out.println("TEST");
        Person person = new Person();
        try
        {
            if(!custom_date_flag)
            {
                Gson gson = new Gson();
                person = gson.fromJson(data_json, Person.class);
            }else
            {

                // Creates the json object which will manage the information received
                GsonBuilder builder = new GsonBuilder();
                // Register an adapter to manage the date types as long values
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });
                Gson gson = builder.create();

                person = gson.fromJson(data_json, Person.class);
            }

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace());
            System.out.println("ERROR IN JSON UNMARSHAL");
        }
        return person;
    }
}
