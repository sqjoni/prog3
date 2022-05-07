package fi.tuni.prog3.sisu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DegreeStructureReaderTest {

    /**
     * Tests that the sisureader created has currect language string and getLanguage
     * returns that
     */
    @Test
    void testConstructorAndGetLanguage() {
        DegreeStructureReader reader = new DegreeStructureReader("en");
        String expected = "en";
        String actual = reader.getLanguage();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetLanguage() {
        DegreeStructureReader reader = new DegreeStructureReader("en");
        reader.setLanguage("fi");
        String expected = "fi";
        String actual = reader.getLanguage();
        assertEquals(expected, actual);
    }

    @Test
    public void testReadStructure() {
        //
    }
}
