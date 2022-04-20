package fi.tuni.prog3.sisu;

public class UserData {
    private String name;
    private String studentNumber;
    private String start;
    private String goal;
    private String degreeProgrammeName;
    private String language;

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
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the degreeProgrammeName
     */
    public String getDegreeProgrammeName() {
        return degreeProgrammeName;
    }

    /**
     * @param degreeProgrammeName the degreeProgrammeName to set
     */
    public void setDegreeProgrammeName(String degreeProgrammeName) {
        this.degreeProgrammeName = degreeProgrammeName;
    }

    /**
     * @return the goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     * @param goal the goal to set
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return the studentNumber
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * @param studentNumber the studentNumber to set
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
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
