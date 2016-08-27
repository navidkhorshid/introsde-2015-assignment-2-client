package client;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;
import to.HealthMeasureHistory;
import to.MeasureDefinition;
import to.Person;
import unmarshaller.MeasureHistoryUnmarshal;
import unmarshaller.MeasureTypeUnmarshal;
import unmarshaller.PersonUnmarshal;
import util.PrintXML;
import util.TeePrintStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientRequests {
	private WebTarget service;
	private MediaType mediaType;
	private String url;

	//Constructor for initializing the service and mediatype
	public ClientRequests(MediaType mediaType, String url) throws Exception
	{
		this.mediaType = mediaType;
		ClientConfig clientConfig = new ClientConfig();
		javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);
		this.url = url;
		this.service = client.target(UriBuilder.fromUri(url).build());
		//This line enables writing to a file and printing at the same time
		writeToLog(outputJsonXml());
	}

	//Writing to LOG file
	public void writeToLog(String param) throws Exception
	{
		//Printing in Log file as well as console out
		try
		{
			FileOutputStream file = new FileOutputStream("client-server-"+ param+ ".log");
			TeePrintStream tee = new TeePrintStream(file, System.out);
			System.setOut(tee);
		}catch (Exception e){}
	}

	//Helps to choose which file to write in based on MediaType
	public String outputJsonXml() throws Exception
	{
		if (this.mediaType.equals(mediaType.APPLICATION_XML_TYPE))
			return "xml";
		else if(this.mediaType.equals(mediaType.APPLICATION_JSON_TYPE))
			return "json";
		else return "else";
	}

	//To come up with OK or ERROR messages
	public String resultChanger(int status) throws Exception
	{
		String result = "ERROR";
		switch (status)
		{
			case 200:
				result = "OK";
				break;
			case 201:
				result = "OK";
				break;
			case 202:
				result = "OK";
				break;
			default:
				result = "ERROR";
				break;
		}
		return result;
	}

	//Print title for requests
	public void printTitle(String no, String request, String path, int status, String result, int accept_content) throws Exception
	{
		String accept_content_string = "";
		switch (accept_content)
		{
			//none
			case 0:
				accept_content_string = "";
				break;
			//accept
			case 1:
				accept_content_string = " Accept: " + this.mediaType.toString() ;
				break;
			//content
			case 2:
				accept_content_string = " Content-type: " + this.mediaType.toString();
				break;
			//both
			case 3:
				accept_content_string = " Accept: " + this.mediaType.toString() + " Content-type: " + this.mediaType.toString();
				break;
		}

		//Request #[NUMBER]: [HTTP METHOD] [URL] Accept: [TYPE] Content-type: [TYPE]
		System.out.println("\nRequest #" + no + ": "+request+" " + this.url+"/"+path + accept_content_string);
		//=> Result: [RESPONSE STATUS = OK, ERROR]
		System.out.println("=> Result: "+result);
		//=> HTTP Status: [HTTP STATUS CODE = 200, 404, 500 ...]
		System.out.println("=> HTTP Status: " + status);
	}

	//Print XML or JSON in pretty format
	public void printBody(String data) throws Exception
	{
		if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
			System.out.println(new JSONObject(data).toString(2));
		else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
			System.out.println(new PrintXML().format(data));
		else System.out.println(data);
	}

	public long[] requestFirstLastPersonId() throws Exception
	{
		//GET PEOPLE
		String path = "person/firstlast";
		Response response = this.service.path(path).request().accept(MediaType.TEXT_PLAIN).get();
		long[] twoId = new long[2];
		String[] Id = response.readEntity(String.class).split(":");
		twoId[0] = Long.parseLong(Id[0]);
		twoId[1] = Long.parseLong(Id[1]);
		return twoId;
	}


	public String request1() throws Exception
	{
		//GET PEOPLE
		String path = "person";
		Response response = this.service.path(path).request().accept(mediaType).get();
		String output_people = response.readEntity(String.class);

		//if there are less than 3 results show ERROR as for Result
		PersonUnmarshal personUnmarshal = new PersonUnmarshal();
		if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
		{
			if(personUnmarshal.getPersonsJSON(output_people).size() < 3) {
				printTitle("1", "GET", path, response.getStatus(), "ERROR", 1);
			}else {
				printTitle("1", "GET", path, response.getStatus(), "OK", 1);
				printBody(output_people);
			}
		}else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
		{
			if(personUnmarshal.getPersons(output_people).size() < 3) {
				printTitle("1", "GET", path, response.getStatus(), "ERROR", 1);
			}else {
				printTitle("1", "GET", path, response.getStatus(), "OK", 1);
				printBody(output_people);
			}
		}
		return output_people;
	}


	public String request2(long id, Boolean print) throws Exception
	{
		//GET PERSON BY ID
		String path = "person/"+id;
		Response response = this.service.path(path).request().accept(this.mediaType).get();
		String responseString = response.readEntity(String.class);

		//It is because I will use request2 again but without needing to print
		if(print)
		{
			printTitle("2", "GET", path, response.getStatus(), resultChanger(response.getStatus()), 1);
			printBody(responseString);
		}

		return responseString;
	}


	public void request3(long id, Person person, String new_Name) throws Exception
	{
		//I have to keep both of them in order to compare if it has acutally changed
		String person_old_name = person.getFirstname();
		person.setFirstname(new_Name);

		String path = "person/"+id;
		//just initialization with something
		Response response = null;

		try
		{
			if (this.mediaType.equals(mediaType.APPLICATION_XML_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).put(Entity.xml(person));
			else if(this.mediaType.equals(mediaType.APPLICATION_JSON_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).put(Entity.json(person));

		}catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		//Now that I am done with PUT request I have to do REQ2 to see if it has changed
		String request2Result = this.request2(id, false);
		String result = "OK";
		//result would be OK unless the new firstname is the same as the old one
		//There might be a case where the new Value "Chester" was already the name of the first Person
		if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
		{
			if (new PersonUnmarshal().getPersonJSON(request2Result, false).getFirstname().equals(person_old_name))
				result = "ERROR";
		}else if(this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
		{
			if (new PersonUnmarshal().getPerson(request2Result).getFirstname().equals(person_old_name))
				result = "ERROR";
		}
		printTitle("3", "PUT", path, response.getStatus(), result, 2);
	}

	public long request4(Person person) throws Exception
	{
		String path = "person";
		//just initialization with something
		Response response = null;

		if (this.mediaType.equals(mediaType.APPLICATION_XML_TYPE))
			response = this.service.path(path).request().accept(this.mediaType).post(Entity.xml(person));
		else if(this.mediaType.equals(mediaType.APPLICATION_JSON_TYPE))
			response = this.service.path(path).request().accept(this.mediaType).post(Entity.json(person));

		String responseString = response.readEntity(String.class);
		String result = "ERROR";

		long created_idPerson = 0;
		//I had to do a little trick to get ID in firstname of XML or JSON. Because I couldnt return
		//ID as an integer nor string while using POST method
		if(this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
			created_idPerson = Long.parseLong(new PersonUnmarshal().getPersonJSON(responseString, true).getFirstname().split("::")[0]);
		else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
			created_idPerson = Long.parseLong(new PersonUnmarshal().getPerson(responseString).getFirstname().split("::")[0]);

		//because we need to return in as well
		person.setIdPerson(created_idPerson);

		//If it has an ID in the body of the message and the status is "20x"
		if(created_idPerson !=0 && resultChanger(response.getStatus()).equals("OK"))
			result = "OK";

		printTitle("4", "POST", path, response.getStatus(), result, 3);
		if(result.equals("OK"))
			printBody(responseString);

		return person.getIdPerson();
	}


	public void request5(long id) throws Exception
	{
		String path = "person/"+id;
		Response response = this.service.path(path).request().delete();


		//Now that I am done with DELETE request I have to do REQ2 to see if it is gone or not
		Response responseRequest2 = this.service.path(path).request().accept(this.mediaType).get();

		String result = "ERROR";
		//if request two is 404 and delete was successful make the result OK
		if (responseRequest2.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
				&& resultChanger(response.getStatus()).equals("OK"))
			result = "OK";

		printTitle("5", "DELETE", path, response.getStatus(), result, 0);
	}

	public List<HealthMeasureHistory> request6(long[] idPerson_two , List<String> measureTypes) throws Exception
	{
		Response response = null;
		String path = "";
		String output_measures = "";
		//for printing in case nothing found and Error
		List<String> error_paths = new ArrayList<String>();
		boolean flag = false;
		//for printing in case OK
		List<String> ok_paths = new ArrayList<String>();
		List<String> ok_output = new ArrayList<String>();
		List<Response> ok_responses = new ArrayList<Response>();
		List<HealthMeasureHistory> healthMeasureHistories = new ArrayList<HealthMeasureHistory>();

		for(int i = 0 ; i < idPerson_two.length ; i++)
		{
			for(int j = 0 ; j < measureTypes.size(); j++)
			{
				path = "person/"+idPerson_two[i]+"/"+measureTypes.get(j);
				response = this.service.path(path).request().accept(mediaType).get();
				output_measures = response.readEntity(String.class);

				MeasureHistoryUnmarshal measureHistoryUnmarshal = new MeasureHistoryUnmarshal();
				List<HealthMeasureHistory> measures = new ArrayList<HealthMeasureHistory>();

				if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
					measures = measureHistoryUnmarshal.getHealthMeasureHistoriesJSON(output_measures);
				else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
					measures = measureHistoryUnmarshal.getHealthMeasureHistories(output_measures);

				if(measures.size() > 0)
				{
					//if it runs even once the results will be OK
					flag = true;
					//keep in mind the OK path and responses
					ok_responses.add(response);
					ok_output.add(output_measures);
					ok_paths.add(path);
					//Store one measureId and measureType
					HealthMeasureHistory healthMeasureHistory = new HealthMeasureHistory();

					healthMeasureHistory.setMid(measures.get(0).getMid());
					healthMeasureHistory.setValue(measures.get(0).getValue());

					MeasureDefinition measureDefinition = new MeasureDefinition();
					measureDefinition.setMeasureType(measureTypes.get(j));

					healthMeasureHistory.setMeasureDefinition(measureDefinition);
					healthMeasureHistory.setCreated(measures.get(0).getCreated());

					Person person = new Person();
					person.setIdPerson(idPerson_two[i]);

					healthMeasureHistory.setPerson(person);

					healthMeasureHistories.add(healthMeasureHistory);
				}else //in case no measure found
				{
					//keep in mind error paths
					error_paths.add(path);
				}
			}
		}

		String result = "ERROR";
		int status = 404;
		// if successful show all attempts which are OK
		if(flag)
		{
			result = "OK";
			for(int i=0; i < ok_responses.size(); i++)
			{
				status = ok_responses.get(i).getStatus();
				printTitle("6", "GET", ok_paths.get(i), status, result, 1);
				printBody(ok_output.get(i));
			}
		}else
		{
			// In case all was empty show every attempt
			for(String p: error_paths)
			{
				printTitle("6", "GET", p, status, result, 1);
			}
		}
		//Could be empty if 404
		return healthMeasureHistories;
	}

	public HealthMeasureHistory request7(HealthMeasureHistory healthMeasureHistory)
	{
		HealthMeasureHistory tmp = new HealthMeasureHistory();
		try
		{
			String path = "person/"+healthMeasureHistory.getPerson().getIdPerson()+"/"+
					healthMeasureHistory.getMeasureDefinition().getMeasureType()+"/"+healthMeasureHistory.getMid();
			Response response = this.service.path(path).request().accept(mediaType).get();
			String responseString = response.readEntity(String.class);

			printTitle("7", "GET", path, response.getStatus(), resultChanger(response.getStatus()), 1);
			printBody(responseString);

			if(this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
				tmp = new MeasureHistoryUnmarshal().getHealthMeasureHistoryJSON(responseString, false);
			else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
				tmp = new MeasureHistoryUnmarshal().getHealthMeasureHistory(responseString);

		}catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return tmp;

	}

	public HealthMeasureHistory request8(long personId, String measureType, HealthMeasureHistory healthMeasureHistory)
	{
		try
		{
			//make a new array of integer, but put only the first personId inside
			long[] request8_firstPerson = new long[1];
			request8_firstPerson[0] = personId;
			//make a new list of String but only put the first measure_types inside
			List<String> request8_measureType = new ArrayList<String>();
			request8_measureType.add(measureType);
			//Send the first personId and a measureType to Request 6
			List<HealthMeasureHistory> measure_req6 = this.request6(request8_firstPerson ,request8_measureType);
			//save count value
			int request6_count = measure_req6.size();
			//send to r8
			String path = "person/"+personId+"/"+measureType;
			//just initialization with something
			Response response = null;
			if (this.mediaType.equals(mediaType.APPLICATION_XML_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).post(Entity.xml(healthMeasureHistory));
			else if(this.mediaType.equals(mediaType.APPLICATION_JSON_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).post(Entity.json(healthMeasureHistory));

			String responseString = response.readEntity(String.class);
			//send to R6
			measure_req6 = this.request6(request8_firstPerson ,request8_measureType);
			//+1 OK else Error
			if(measure_req6.size() != (request6_count + 1) && resultChanger(response.getStatus()).equals("ERROR"))
			{
				printTitle("8", "POST", path, response.getStatus(), "ERROR", 2);
			}else
			{
				printTitle("8", "POST", path, response.getStatus(), "OK", 3);
				printBody(responseString);
			}

			int created_mid = 0;
			if(this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
				created_mid = new MeasureHistoryUnmarshal().getHealthMeasureHistoryJSON(responseString, true).getMid();
			else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
				created_mid = new MeasureHistoryUnmarshal().getHealthMeasureHistory(responseString).getMid();

			//because we need to return in as well
			healthMeasureHistory.setMid(created_mid);

		}catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		return healthMeasureHistory;
	}


	public List<String> request9() throws Exception
	{
		//GET MeasureTypes
		String path = "measureTypes";
		Response response = this.service.path(path).request().accept(this.mediaType).get();
		String output_measureTypes = response.readEntity(String.class);

		//if there are less than 3 results show ERROR as for Result
		MeasureTypeUnmarshal measureTypeUnmarshal = new MeasureTypeUnmarshal();
		List<String> measure_types = new ArrayList<String>();

		if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
			measure_types = measureTypeUnmarshal.getMeasureTypesJSON(output_measureTypes);
		else if (this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
			measure_types = measureTypeUnmarshal.getMeasureTypes(output_measureTypes);

		if (measure_types.size() < 3) {
			printTitle("9", "GET", path, response.getStatus(), "ERROR", 1);
		}else {
			printTitle("9", "GET", path, response.getStatus(), "OK", 1);
			printBody(output_measureTypes);
		}
		return measure_types;
	}

	//EXTRA FEATURES
	public void request10(long personId, String measureType, HealthMeasureHistory healthMeasureHistory, String new_value)
	{
		try
		{
			String path = "person/"+personId+"/"+measureType+"/"+healthMeasureHistory.getMid();
			Response response = null;
			//I have to change the value of healthmeasurehistory
			healthMeasureHistory.setValue(new_value);

			if (this.mediaType.equals(mediaType.APPLICATION_XML_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).put(Entity.xml(healthMeasureHistory));
			else if(this.mediaType.equals(mediaType.APPLICATION_JSON_TYPE))
				response = this.service.path(path).request().accept(this.mediaType).put(Entity.json(healthMeasureHistory));


			//Now that I am done with PUT request I have to do REQ7 to see if it has changed
			Person person = new Person();
			person.setIdPerson(personId);
			healthMeasureHistory.setPerson(person);

			MeasureDefinition measureDefinition = new MeasureDefinition();
			measureDefinition.setMeasureType(measureType);
			healthMeasureHistory.setMeasureDefinition(measureDefinition);

			HealthMeasureHistory request7Result = this.request7(healthMeasureHistory);

			String result = "ERROR";
			if (this.mediaType.equals(MediaType.APPLICATION_JSON_TYPE))
			{
				if (request7Result.getValue().equals(new_value)
						&& resultChanger(response.getStatus()).equals("OK"))
					result = "OK";
			}else if(this.mediaType.equals(MediaType.APPLICATION_XML_TYPE))
			{
				if (request7Result.getValue().equals(new_value)
						&& resultChanger(response.getStatus()).equals("OK"))
					result = "OK";
			}
			printTitle("10", "PUT", path, response.getStatus(), result, 2);

		}catch (Exception e)
		{
			System.out.println(e.getCause());
		}
	}

	public void request11(long personId, String measureType, String after, String before) throws Exception
	{
		String path = "person/"+personId+"/"+measureType;
		Response response = this.service.path(path).queryParam("before",before).queryParam("after",after).request().accept(this.mediaType).get();
		String responseString = response.readEntity(String.class);

		printTitle("11", "GET", path, response.getStatus(), resultChanger(response.getStatus()), 1);
		printBody(responseString);
	}

	public void request12(String measureType, String min, String max) throws Exception
	{
		String path = "person";
		Response response = this.service.path(path).queryParam("measureType",measureType).queryParam("max",max)
				.queryParam("min",min).request().accept(this.mediaType).get();
		String responseString = response.readEntity(String.class);
		printTitle("12", "GET", path, response.getStatus(), resultChanger(response.getStatus()), 1);
		printBody(responseString);
	}

}