package fi.tuni.prog3.sisu;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class AppTest extends ApplicationTest {   
    
    Scene scene;
    private static String css;

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

    
    @Test
    public void testDegreeComboBox() {
        FxAssert.verifyThat("#degreeComboBox", ComboBoxMatchers.hasItems(269));
    }

    @Test
    public void testGetStudent() {
        App.setUserData("Tom", "123", "2021", "2026",
        "Teknis-taloudellinen kandidaattiohjelma", "fi");
        var user = App.getStudent();
        String expResult = "Student Tom, studentNumber: 12320212026Teknis-taloudellinen kandidaattiohjelmafi";
        String result = App.getStudent().toString() + user.getStart() + user.getGoal()
        + user.getDegreeProgrammeName() + user.getLanguage();;
        assertEquals(expResult, result);
    }

    @Test
    public void testPrimaryWindow(FxRobot robot) {
        robot.clickOn("#nameField");
        robot.write("Tom", 500);
        robot.clickOn("#studentNumberField");
        robot.write("123", 500);
        FxAssert.verifyThat("#nameField", TextInputControlMatchers.hasText("Tom"));
        FxAssert.verifyThat("#studentNumberField", TextInputControlMatchers.hasText("123"));
    }


    @Test
    public void testSetUserData() {
        App.setUserData("Tom", "123", "2021", "2026",
        "Teknis-taloudellinen kandidaattiohjelma", "fi");
        var user = App.getStudent();
        String expResult = "Student Tom, studentNumber: 12320212026Teknis-taloudellinen kandidaattiohjelmafi";
        String result = App.getStudent().toString() + user.getStart() + user.getGoal()
        + user.getDegreeProgrammeName() + user.getLanguage();;
        assertEquals(expResult, result);
    }

    @Test
    public void testLanguageSwitch(FxRobot robot){
        FxAssert.verifyThat("#toggleLanguage", LabeledMatchers.hasText("fi"));
        robot.clickOn("#toggleLanguage");
        FxAssert.verifyThat("#toggleLanguage", LabeledMatchers.hasText("en"));
        robot.clickOn("#saveButton");
        FxAssert.verifyThat("#nameLabel", LabeledMatchers.hasText("Name: *"));
    }

    @Test
    public void testSaveUserData() throws IOException {
        App.setUserData("Tom", "69420", "2021", "2026",
        "Teknis-taloudellinen kandidaattiohjelma", "fi");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UserData userData = App.getStudent();
        String userNumber = userData.getStudentNumber();
        FileWriter fileWriter = new FileWriter(String.format("userdata/%s.json", userNumber));
        gson.toJson(userData, fileWriter);
        fileWriter.close();
        Path path = Paths.get(String.format("userdata/%s.json", userNumber));
        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            fail("File isn't found");
        }
    }
}
