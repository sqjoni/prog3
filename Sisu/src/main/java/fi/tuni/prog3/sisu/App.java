package fi.tuni.prog3.sisu;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the JavaFX application.
 */
public class App extends Application {

    private static Scene scene;
    private static UserData userData;
    private static String css;
    private static JSONObject selectionProgramInfo;

    /**
     * Starts the program by loading the fxml-files and the css-file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        scene = new Scene(fxmlLoader.load(), 960, 720);
        PrimaryController sceneControl = fxmlLoader.getController();
        sceneControl.addDegreePrograms();
        css = this.getClass().getResource("primary.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns the selectionProgramInfo.
     * @return the selectionProgramInfo
     */
    public static JSONObject getSelectionProgramInfo() {
        return selectionProgramInfo;
    }

    /**
     * Sets the selectionProgramInfo.
     * @param selectionProgramInfo the selectionProgramInfo to set
     */
    public static void setSelectionProgramInfo(JSONObject selectionProgramInfo) {
        App.selectionProgramInfo = selectionProgramInfo;
    }

    /**
     * When the program is going to quit, saves the users data.
     */
    @Override
    public void stop() throws IOException {
        if (userData != null) {
            saveUserData();
        }
    }

    /**
     * Saves the user data into a json-file.
     * @throws IOException When the file cannot be created
     */
    static void saveUserData() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userNumber = userData.getStudentNumber();
        FileWriter fileWriter = new FileWriter(String.format("userdata/%s.json", userNumber));
        gson.toJson(userData, fileWriter);
        fileWriter.close();
    }

    /**
     * Sets the root of the app.
     * @param fxml Tells the program that which controller should be used
     * @throws IOException When the file cannot be located
     */
    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        scene.setRoot(loader.load());

        if (fxml.equals("secondary")) {
            SecondaryController s2Controller = loader.getController();
            s2Controller.addDegreeInfo(userData);
            s2Controller.createUI(userData);
        }

        if (fxml.equals("primary")) {
            PrimaryController s1Controller = loader.getController();
            s1Controller.addDegreePrograms();
        }
    }

    /**
     * Returns the student-object for usage in different classes
     * @return the student-object for usage in different classes
     */
    static UserData getStudent() {
        return userData;
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * If needed, creates a new user with the entered parameters. Otherwise acts as
     * a data setter for the existing user.
     * @param name The name of the student
     * @param studentNumber The students student number
     * @param startYear The degrees start year
     * @param goalYear The degrees end year
     * @param degreeProgramName The name of the degree
     * @param language The preferred language for the degrees and program
     */
    public static void setUserData(String name, String studentNumber, String startYear, String goalYear,
            String degreeProgramName, String language) {
        if (userData == null) {
            userData = new UserData(name, studentNumber, startYear, goalYear, degreeProgramName, language);
        } else {
            userData.setName(name);
            userData.setStudentNumber(studentNumber);
            userData.setStart(startYear);
            userData.setGoal(goalYear);
            userData.setDegreeProgrammeName(degreeProgramName);
            userData.setLanguage(language);
        }
    }
}
