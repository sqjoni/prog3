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
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static UserData userData;
    private static String css;
    private static JSONObject selectionProgramInfo;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
        scene = new Scene(fxmlLoader.load(), 640, 480);
        PrimaryController sceneControl = fxmlLoader.getController();
        sceneControl.addDegreePrograms();
        css = this.getClass().getResource("primary.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @return the selectionProgramInfo
     */
    public static JSONObject getSelectionProgramInfo() {
        return selectionProgramInfo;
    }

    /**
     * @param selectionProgramInfo the selectionProgramInfo to set
     */
    public static void setSelectionProgramInfo(JSONObject selectionProgramInfo) {
        App.selectionProgramInfo = selectionProgramInfo;
    }

    @Override
    public void stop() throws IOException {
        if (userData != null) {
            saveUserData();
        }
    }

    private void saveUserData() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userNumber = userData.getStudentNumber();
        FileWriter fileWriter = new FileWriter(String.format("userdata/%s.json", userNumber));
        gson.toJson(userData, fileWriter);
        fileWriter.close();
    }

    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        scene.setRoot(loader.load());

        // TODO luo secondarycontrollerissa metodit, joilla lisätään
        // tutkintorakenteet ja kutsu niitä tässä.
        if (fxml.equals("secondary")) {
            SecondaryController s2Controller = loader.getController();
            s2Controller.addDegreeInfo(userData);
            s2Controller.createUI(userData);
        }

        // TODO tässä täytyy kans tallentaa tietoja
        if (fxml.equals("primary")) {
            PrimaryController s1Controller = loader.getController();
            s1Controller.addDegreePrograms();
        }
    }

    static void setUserData(UserData newStudent) {
        userData = newStudent;
    }

    static UserData getStudent() {
        return userData;
    }

    public static void main(String[] args) {
        launch();
    }

}