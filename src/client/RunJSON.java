package client;

import to.HealthMeasureHistory;
import to.HealthProfile;
import to.Person;
import unmarshaller.PersonUnmarshal;

import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Navid on 11/23/2015.
 */
public class RunJSON {

    public static void main(String[] args) {
        try {
            String base_url = "https://dry-ocean-82608.herokuapp.com/ehealth";
            ClientRequests clientRequestsJSON = new ClientRequests(MediaType.APPLICATION_JSON_TYPE, base_url);

            //STEP 3.1 - REQUEST 1
            String people = clientRequestsJSON.request1();

            //STEP 3.2 - REQUEST 2
            //[0] is first_person_id and [1] is last_person_id
            long[] personId_FirstLast = clientRequestsJSON.requestFirstLastPersonId();
            String person_first_string = clientRequestsJSON.request2(personId_FirstLast[0], true);

            //STEP 3.3 - REQUEST 3
            Random rand = new Random();
            int  n = rand.nextInt(100000) + 1;
            String new_name = "Chester" + n; // This change is made because after first time run of the client
            // although the name might change but it will throw Error as result
            // this will reduce the chances of this type of error
            // since execute.client is an automated run of project
            // I should be damn unlucky to get an error during TA's evaluation :D
            // but if it happens I will quit DRINKING WEISS BIER
            PersonUnmarshal personUnmarshal = new PersonUnmarshal();
            clientRequestsJSON.request3(personId_FirstLast[0], personUnmarshal.getPersonJSON(person_first_string, false), new_name);


            //STEP 3.4 - REQUEST 4
            List<HealthProfile> list = new ArrayList<HealthProfile>();
            //ENTER data for Weight
            HealthProfile healthProfile = new HealthProfile();
            healthProfile.setValue("78.9");
            healthProfile.setMeasure("weight");
            list.add(healthProfile);
            //ENTER data for height
            HealthProfile healthProfile1 = new HealthProfile();
            healthProfile1.setValue("172");
            healthProfile1.setMeasure("height");
            list.add(healthProfile1);
            //Enter personal data
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Person person = new Person();
            person.setFirstname("Chuck");
            person.setLastname("Norris");
            person.setBirthdate(new java.util.Date(df.parse("1945-01-01").getTime()));
            person.setMeasure(list);

            long personIdOfReq4 = clientRequestsJSON.request4(person);

            //STEP 3.5 - REQUEST 5
            clientRequestsJSON.request5(personIdOfReq4);
            clientRequestsJSON.request2(personIdOfReq4, true);

            //STEP 3.6 - REQUEST 9
            List<String> measure_types = clientRequestsJSON.request9();

            //STEP 3.7 - REQUEST 6
            //instead of getting only a measureId and measureType I am getting the whole list
            //of OK results because for request 8 I also need the count for example
            List<HealthMeasureHistory> measure_req6 = clientRequestsJSON.request6(personId_FirstLast, measure_types);

            //STEP 3.8 - REQUEST 7
            clientRequestsJSON.request7(measure_req6.get(0));

            //STEP 3.9 - REQUEST 8
            HealthMeasureHistory healthMeasureHistory = new HealthMeasureHistory();
            healthMeasureHistory.setValue("72");
            healthMeasureHistory.setCreated(new java.util.Date(df.parse("2011-12-09").getTime()));
            HealthMeasureHistory req8 = clientRequestsJSON.request8(personId_FirstLast[0], measure_types.get(0), healthMeasureHistory);

            //STEP 3.10 - REQUEST 10
            clientRequestsJSON.request10(personId_FirstLast[0], measure_types.get(0), req8, "90");

            //STEP 3.11 - REQUEST 11
            clientRequestsJSON.request11(personId_FirstLast[0],measure_types.get(0), "01-12-2011", "01-01-2016");

            //STEP 3.12 - REQUEST 12
            clientRequestsJSON.request12(measure_types.get(0), "80", "90");


        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
