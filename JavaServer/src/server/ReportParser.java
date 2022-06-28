package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Timestamp;
import java.util.Date;

public class ReportParser {

  private static JSONParser parser = new JSONParser();

  public static Report parseReport(String parserJSON) {
    Report report = null;
    try {
      JSONObject jsonObject = (JSONObject) (parser.parse(String.valueOf(parserJSON)));
      float longitude = Float.parseFloat((String) (jsonObject.get("longitude")));
      float latitude = Float.parseFloat((String) (jsonObject.get("latitude")));
      String reason = (String) jsonObject.get("reason");
      report = new Report(longitude, latitude, reason);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    return report;
  }


}
