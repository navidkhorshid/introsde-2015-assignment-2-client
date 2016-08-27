package unmarshaller;

import com.google.gson.Gson;
import to.MeasureTypes;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Navid on 11/6/2015.
 */
public class MeasureTypeUnmarshal
{
    public MeasureTypeUnmarshal(){}

    public List<String> getMeasureTypes(String data_xml)
    {
        MeasureTypes measureTypes = new MeasureTypes();
        try
        {
            JAXBContext jc = JAXBContext.newInstance(MeasureTypes.class);
            //System.out.println("Output from XML");
            Unmarshaller um = jc.createUnmarshaller();
            StringReader reader = new StringReader(data_xml);
            measureTypes = (MeasureTypes) um.unmarshal(reader);
        }catch (Exception e) {}
        return measureTypes.getMeasureType();
    }

    public List<String> getMeasureTypesJSON(String data_json)
    {
        MeasureTypes measureTypes = new MeasureTypes();
        try
        {
            Gson gson = new Gson();
            String jsonInString = data_json;
            measureTypes = gson.fromJson(jsonInString, MeasureTypes.class);
        }catch (Exception e) {}
        return measureTypes.getMeasureType();
    }
}
