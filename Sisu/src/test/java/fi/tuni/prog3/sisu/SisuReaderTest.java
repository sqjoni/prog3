package fi.tuni.prog3.sisu;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SisuReaderTest {
    @Test
    void testFalseURL() {
        try {
            SisuReader sisureader = new SisuReader();
            JSONObject jsonobject = (JSONObject) sisureader.getModuleById("non-existing-degree");
            String expectedResult = null;
            Assertions.assertEquals(jsonobject.get("cause"), expectedResult);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(SisuReaderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
