package fi.tuni.prog3.sisu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UserDataTest {
    private String name = "nimi";
    private String studentNumber = "1111";
    private String start = "2020";
    private String goal = "2025";
    private String degreeProgrammeName = "Teknis-Taloudellinen kandi";
    private String language = "fi";
    private String courseString = "kurssi";

    @Test
    void testUpdateCoursesAddsCourse() {
        UserData user = new UserData(name, studentNumber, start, goal, degreeProgrammeName, language);
        List<String> courses = updateCourses(user);
        String expected = courseString;
        String actual = courses.get(0);
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateCoursesRemovesCourse() {
        UserData user = new UserData(name, studentNumber, start, goal, degreeProgrammeName, language);
        updateCourses(user);
        List<String> courses = updateCourses(user);
        assertTrue(courses.size() == 0);
    }

    private List<String> updateCourses(UserData user) {
        user.updateCourses(courseString);
        return user.getCourses();
    }
}
