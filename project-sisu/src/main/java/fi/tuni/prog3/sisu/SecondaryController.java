package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;

public class SecondaryController {
    @FXML
    GridPane mainGridPane;

    @FXML
    Button backButton;

    // TODO tässä täytyy kans tallentaa tietoja
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    // Tuodaan opiskelijan tiedot, mitkä määritetty primary näkymässä
    // (Ei välttämättä tarpeellista testailin vaan miten tietoja saa tuotua)
    public void addDegreeInfo(UserData userData) throws MalformedURLException {
        Label studentInfo = new Label(userData.toString());
        studentInfo.setAlignment(Pos.CENTER_LEFT);
        mainGridPane.add(studentInfo, 1, 0);
        // DegreeStructureReader.readStructure(userData.getDegreeProgrammeName());
    }

    // TODO enemmän kun demo
    public void addTreeView(ScrollPane spCourses, String languageCode) throws MalformedURLException {
        TreeItem<String> root = new TreeItem<>();

        // TODO userDatassa täytyis olla toi kieli ja sen valinta täytyis lisää näytölle

        // Get info from sisu and add to root
        DegreeStructureReader reader = new DegreeStructureReader(languageCode);

        reader.readStructure(App.getSelectionProgramInfo(), root);

        root.setExpanded(false);
        TreeView<String> tv = new TreeView<>(root);
        spCourses.setContent(tv);
    }

    // TODO rakenna tän sisällä UI. Kutsu muita tarvittavia funktioita.
    public void createUI(UserData userData) throws MalformedURLException {
        mainGridPane.setHgap(10);

        ScrollPane spCourses = new ScrollPane();
        spCourses.setHbarPolicy(ScrollBarPolicy.NEVER);
        spCourses.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        spCourses.setFitToWidth(true);
        mainGridPane.add(spCourses, 0, 1);

        ScrollPane spCredits = new ScrollPane();
        spCredits.setHbarPolicy(ScrollBarPolicy.NEVER);
        spCredits.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        spCredits.setFitToWidth(true);
        mainGridPane.add(spCredits, 1, 1);

        String languageCode = userData.getLanguage();

        if (languageCode.equals("en")) {
            backButton.setText("Back");
        }

        addTreeView(spCourses, languageCode);
    }
}