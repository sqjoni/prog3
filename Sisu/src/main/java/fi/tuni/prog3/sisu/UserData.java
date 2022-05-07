package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import java.util.List;

/**
 * UserData is a class used to contain and manipulate the users information.
 */
public class UserData {
    private String name;
    private String studentNumber;
    private String start;
    private String goal;
    private String degreeProgrammeName;
    private String language;
    private List<String> completedCourses = new ArrayList<String>();

    /**
     * UserData constructor.
     * @param name The students name
     * @param studentNumber The students student number
     * @param start The start year of the degree
     * @param goal The targeted end year of the degree
     * @param degreeProgrammeName The name of the degree
     * @param language The language of the degree and program
     */
    public UserData(String name, String studentNumber, String start, String goal, String degreeProgrammeName,
            String language) {
        this.setName(name);
        this.setStudentNumber(studentNumber);
        this.setStart(start);
        this.setGoal(goal);
        this.setDegreeProgrammeName(degreeProgrammeName);
        this.setLanguage(language);
    }

    /**
     * Returns the completed courses as a list.
     * @return The completed courses as a list
     */
    public List<String> getCourses() {
        return completedCourses;
    }

    /**
     * Empties the list of courses
     */
    public void clearCourses() {
        completedCourses.clear();
    }

    /**
     * Updates the users list of completed courses.
     * @param courseString The course to be added or removed from the list of
     * completed courses
     */
    public void updateCourses(String courseString) {
        if (completedCourses.contains(courseString)) {
            completedCourses.remove(completedCourses.indexOf(courseString));
        } else {
            completedCourses.add(courseString);
        }
    }

    /**
     * Returns the preferred language.
     * @return The preferred language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the preferred language.
     * @param language The language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns the degree program name.
     * @return the degreeProgrammeName
     */
    public String getDegreeProgrammeName() {
        return degreeProgrammeName;
    }

    /**
     * Sets the degree program name.
     * @param degreeProgrammeName the degreeProgrammeName to set
     */
    public void setDegreeProgrammeName(String degreeProgrammeName) {
        this.degreeProgrammeName = degreeProgrammeName;
    }

    /**
     * Returns the graduation goal.
     * @return the goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Sets the graduation goal.
     * @param goal the goal to set
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Returns the start of the degree.
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets the start of the degree.
     * @param start the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Returns the student number.
     * @return the studentNumber
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * Sets the student number.
     * @param studentNumber the studentNumber to set
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Student " + name + ", studentNumber: " + studentNumber;
    }
}
