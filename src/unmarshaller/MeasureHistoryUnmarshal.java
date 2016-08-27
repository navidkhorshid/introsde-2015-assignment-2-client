package unmarshaller;

import com.google.gson.*;
import to.HealthMeasureHistory;
import to.MeasureHistory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created by Navid on 11/6/2015.
 */
public class MeasureHistoryUnmarshal
{
    public MeasureHistoryUnmarshal(){}

    public List<HealthMeasureHistory> getHealthMeasureHistories(String data_xml)
    {
        MeasureHistory measureHistory = new MeasureHistory();
        try
        {
            JAXBContext jc = JAXBContext.newInstance(MeasureHistory.class);
            //System.out.println("Output from XML");
            Unmarshaller um = jc.createUnmarshaller();
            StringReader reader = new StringReader(data_xml);
            measureHistory = (MeasureHistory) um.unmarshal(reader);
        }catch (Exception e) {}
        return measureHistory.getHealthMeasureHistories();
    }

    public List<HealthMeasureHistory> getHealthMeasureHistoriesJSON(String data_json)
    {
        MeasureHistory measureHistory = new MeasureHistory();
        try
        {
            Gson gson = new Gson();
            measureHistory = gson.fromJson(data_json, MeasureHistory.class);
        }catch (Exception e) {}
        return measureHistory.getHealthMeasureHistories();
    }

    public HealthMeasureHistory getHealthMeasureHistory(String data_xml)
    {
        HealthMeasureHistory healthMeasureHistory = new HealthMeasureHistory();
        try
        {
            JAXBContext jc = JAXBContext.newInstance(HealthMeasureHistory.class);
            //System.out.println("Output from XML");
            Unmarshaller um = jc.createUnmarshaller();
            StringReader reader = new StringReader(data_xml);
            healthMeasureHistory = (HealthMeasureHistory) um.unmarshal(reader);
        }catch (Exception e) {}
        return healthMeasureHistory;
    }

    public HealthMeasureHistory getHealthMeasureHistoryJSON(String data_json, boolean custom_date_flag)
    {
        HealthMeasureHistory healthmeasureHistory = new HealthMeasureHistory();
        try
        {
            if(!custom_date_flag)
            {
                Gson gson = new Gson();
                healthmeasureHistory = gson.fromJson(data_json, HealthMeasureHistory.class);
            }
            else
            {
                // Creates the json object which will manage the information received
                GsonBuilder builder = new GsonBuilder();
                // Register an adapter to manage the date types as long values
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>()
                {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                    {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });
                Gson gson = builder.create();
                healthmeasureHistory = gson.fromJson(data_json, HealthMeasureHistory.class);
        }
        }catch (Exception e)
        {
            System.out.println(e.getCause());
        }
        return healthmeasureHistory;
    }
}
