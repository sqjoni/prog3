
package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class SisuReader {

    private static final String degreeProgrammeURL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
    private static final String getByIdURL = "https://sis-tuni.funidata.fi/kori/api/modules/";
    private static final String getModuleByGroupIdURLP1 = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=";
    private static final String getByGroupIdURLP2 = "&universityId=tuni-university-root-id";
    private static final String getCourseByGroupIdURLP1 = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=";

    private static HttpURLConnection connection;
    private static String jsonString;
    private static BufferedReader reader;
    private static String line;
    private static StringBuffer responseContent;

    /**
     * Constructor for SisuReader
     */
    SisuReader() {

    }

    public Object getDegreeProgramme() throws MalformedURLException {
        return getCourseData(degreeProgrammeURL);
    }

    public Object getModuleByGroupId(String groupId) throws MalformedURLException {
        return getCourseData(getModuleByGroupIdURLP1 + groupId + getByGroupIdURLP2);
    }

    public Object getModuleById(String id) throws MalformedURLException {
        return getCourseData(getByIdURL + id);
    }

    public Object getCourseUnitByGroupId(String groupId) throws MalformedURLException {
        return getCourseData(getCourseByGroupIdURLP1 + groupId + getByGroupIdURLP2);
    }

    /**
     * Pulls data from Sisu api
     * 
     * @param urlString
     * @return
     * @throws MalformedURLException
     */
    private Object getCourseData(String urlString) throws MalformedURLException {

        try {
            responseContent = new StringBuffer();

            URL url = new URL(urlString);

            // Establish connection
            connection = (HttpURLConnection) url.openConnection();

            // Setup connection
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            jsonString = responseContent.toString();

            if (jsonString.startsWith("{")) {
                return new JSONObject(jsonString);
            }

            return new JSONArray(jsonString);

        } catch (IOException ex) {
            Logger.getLogger(SisuReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
