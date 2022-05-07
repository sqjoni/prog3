package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The class for manipulating the first window. This class also performs
 * checks for existing student data and checks if the users entered data is valid.
 */
public class PrimaryController {

    private ArrayList<JSONObject> degreeInfoObjects;

    @FXML
    private TextField nameField;
    @FXML
    private TextField studentNumberField;
    @FXML
    private TextField startField;
    @FXML
    private TextField goalField;
    @FXML
    private Button saveButton;
    @FXML
    private Button emptyButton;
    @FXML
    private ComboBox<String> degreeComboBox;
    @FXML
    private Button toggleLanguage;
    @FXML
    private Label nameLabel;
    @FXML
    private Label studentNumberLabel;
    @FXML
    private Label startYearLabel;
    @FXML
    private Label goalYearLabel;
    @FXML
    private Label degreeProgramLable;
    @FXML
    private Label languagePromptLabel;

    private boolean english = false;

    /**
     * Creates new student object. Switches to secondary view.
     * @throws IOException When the file cannot be located
     */
    @FXML
    private void switchToSecondary() throws IOException {
        String name = nameField.getText();
        String studentNumber = studentNumberField.getText();
        String startYear = startField.getText();
        String goalYear = goalField.getText();
        String language = toggleLanguage.getText();

        if (!isValidStudentInfo(name, studentNumber)) {
            return;
        }

        String degreeProgramName = degreeComboBox.getValue().toString();

        JSONObject programInfoObject = getSelectionDegreeObject(degreeProgramName);

        App.setUserData(name, studentNumber, startYear, goalYear, degreeProgramName, language);

        App.getStudent().clearCourses();

        checkForExistingFile(studentNumber);

        App.setSelectionProgramInfo(programInfoObject);

        App.setRoot("secondary");
    }

    /**
     * Switches the UI-language.
     */
    @FXML
    private void switchLanguage() {
        if (english) {
            setFinnishPrompts();
            english = false;
        } else {
            setEnglishPrompts();
            english = true;
        }
        ChangeDegreeLanguage();
    }

    /**
     * Performs a check for an existing user file, if one is found
     * its courses are imported to the program.
     * @param studentNumber The student number, with which to perform the data check
     */
    private void checkForExistingFile(String studentNumber) {
        var user = App.getStudent();
        try {
            Gson gson = new Gson();
            var reader = new BufferedReader(new FileReader(String.format(
                "userdata/%s.json", studentNumber
            )));
            HashMap<?, ?> map = gson.fromJson(reader, HashMap.class);
            for (HashMap.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey().equals("completedCourses")) {
                    String courses = entry.getValue().toString();
                    courses = courses.substring(1);
                    courses = courses.substring(0, courses.length() - 1);
                    List<String> temporaryList = Arrays.asList(courses.split(", "));
                    for (String course : temporaryList) {
                        if (!course.equals("")) {
                            user.updateCourses(course);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Sets Finnish prompts for the application.
     */
    private void setFinnishPrompts() {
        toggleLanguage.setText("fi");
        nameLabel.setText("Nimi: *");
        studentNumberLabel.setText("Opiskelijanumero: *");
        startYearLabel.setText("Aloitusvuosi:");
        goalYearLabel.setText("Tavoitevuosi:");
        degreeProgramLable.setText("Tutkinto-ohjelma:");
        degreeComboBox.setPromptText("Tutkinto-ohjelmat");
        saveButton.setText("Tallenna");
        emptyButton.setText("Tyhjennä");
        languagePromptLabel.setText("Kieli:");
    }

    /**
     * Sets English prompts for the application.
     */
    private void setEnglishPrompts() {
        toggleLanguage.setText("en");
        nameLabel.setText("Name: *");
        studentNumberLabel.setText("Studentnumber: *");
        startYearLabel.setText("Start year:");
        goalYearLabel.setText("Goal year:");
        degreeProgramLable.setText("Degree program:");
        degreeComboBox.setPromptText("Degree programs");
        saveButton.setText("Save");
        emptyButton.setText("Empty");
        languagePromptLabel.setText("Language:");
    }

    /**
     * Checking for mandatory information.
     * @param name The input name to be checked
     * @param studentNumber The input student number to be checked
     * @return The boolean true when the filled in information is ok
     */
    private boolean isValidStudentInfo(String name, String studentNumber) {
        boolean valid = true;

        String mandatoryText = "Pakollinen tieto";
        String selectProgram = "Valitse tutkinto-ohjelma";

        if (english) {
            mandatoryText = "Mandatory information";
            selectProgram = "Select degree program";
        }

        if (name.equals("")) {
            nameField.setPromptText(mandatoryText);
            valid = false;
        }
        if (studentNumber.equals("")) {
            studentNumberField.setPromptText(mandatoryText);
            valid = false;
        }
        if (degreeComboBox.getSelectionModel().isEmpty()) {
            degreeComboBox.setPromptText(selectProgram);
            valid = false;
        }
        return valid;
    }

    /**
     * Clear user input from all fields.
     */
    @FXML
    private void emptyTextFields() {
        nameField.setText("");
        studentNumberField.setText("");
        startField.setText("");
        goalField.setText("");
    }

    /**
     * Adds the selectable degrees to the presented combo box.
     * @throws MalformedURLException When the URL doesn't match anything
     */
    public void addDegreePrograms() throws MalformedURLException {
        SisuReader sisuReader = new SisuReader();
        JSONObject courseData = (JSONObject) sisuReader.getDegreeProgramme();

        JSONArray allDegreeProgrammes = (JSONArray) courseData.get("searchResults");

        degreeInfoObjects = new ArrayList<JSONObject>();

        for (int i = 0; i < allDegreeProgrammes.length(); ++i) {
            JSONObject degree = allDegreeProgrammes.getJSONObject(i);

            String degreeId = degree.getString("id");

            JSONObject degreeInfo = (JSONObject) sisuReader.getModuleById(degreeId);

            degreeInfoObjects.add(degreeInfo);
        }

        ChangeDegreeLanguage();

        degreeComboBox.setEditable(true);

        // Set combobox hidden after entering a character
        degreeComboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                degreeComboBox.hide();
            }
        });

        // Eventhandler for releasing a key which delimits the search results
        degreeComboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {

            private int caretPos;
            private boolean moveCaretToPos = false;
            private ObservableList<String> data = degreeComboBox.getItems();

            @Override
            /**
             * Handler for different keypresses that allows intuitive use of arrow keys and
             * other non-character keys
             */
            public void handle(KeyEvent event) {
                if (null != event.getCode())
                    switch (event.getCode()) {
                    case UP:
                        caretPos = -1;
                        moveCaret(degreeComboBox.getEditor().getText().length());
                        return;
                    case DOWN:
                        if (!degreeComboBox.isShowing()) {
                            degreeComboBox.show();
                        }
                        caretPos = -1;
                        moveCaret(degreeComboBox.getEditor().getText().length());
                        return;
                    case BACK_SPACE:
                        moveCaretToPos = true;
                        caretPos = degreeComboBox.getEditor().getCaretPosition();
                        break;
                    case DELETE:
                        moveCaretToPos = true;
                        caretPos = degreeComboBox.getEditor().getCaretPosition();
                        break;
                    default:
                        break;
                    }

                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.isControlDown()
                        || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END
                        || event.getCode() == KeyCode.TAB) {
                    return;
                }

                // Create an observable list for items that contain entered
                // character array
                ObservableList<String> list = FXCollections.observableArrayList();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).toString().toLowerCase()
                            .startsWith(degreeComboBox.getEditor().getText().toLowerCase())) {
                        list.add(data.get(i));
                    }
                }
                String currentText = degreeComboBox.getEditor().getText();

                // Update the items in combobox dropdown list
                degreeComboBox.setItems(list);
                degreeComboBox.getEditor().setText(currentText);

                // Set the cursor to the end of the input box
                if (!moveCaretToPos) {
                    caretPos = -1;
                }
                moveCaret(currentText.length());
                if (!list.isEmpty()) {
                    degreeComboBox.show();
                }
            }

            // Function for moving the cursor to the correct position
            private void moveCaret(int textLength) {
                if (caretPos == -1) {
                    degreeComboBox.getEditor().positionCaret(textLength);
                } else {
                    degreeComboBox.getEditor().positionCaret(caretPos);
                }
                moveCaretToPos = false;
            }

        });

    }

    /**
     * Adds the module name to the degreebox.
     * @param degreeNameObject The object from which the name is pulled
     */
    private void addNameToDegreebox(JSONObject degreeNameObject) {
        String degreesName = getPreferredLanguageString(degreeNameObject);
        degreeComboBox.getItems().add(degreesName);
    }

    /**
     * Changes the language of the degree
     */
    private void ChangeDegreeLanguage() {
        degreeComboBox.getItems().clear();
        for (JSONObject jsonObject : degreeInfoObjects) {
            JSONObject degreeNameObject = jsonObject.getJSONObject("name");
            addNameToDegreebox(degreeNameObject);
        }
    }

    /**
     * Returns the string representation of the preferred language.
     * @param degreeNameObject
     * @return The string representation of the preferred language
     */
    private String getPreferredLanguageString(JSONObject degreeNameObject) {
        if (english && degreeNameObject.has("en")) {
            return degreeNameObject.getString("en");
        }
        if (degreeNameObject.has("fi")) {
            return degreeNameObject.getString("fi");
        }
        return degreeNameObject.getString("en");
    }

    /**
     * Returns the selected degree.
     * @param name The name of the wanted object
     * @return The selected degree
     */
    private JSONObject getSelectionDegreeObject(String name) {
        for (JSONObject jsonObject : degreeInfoObjects) {
            JSONObject degreeNameObject = jsonObject.getJSONObject("name");
            if (getPreferredLanguageString(degreeNameObject).equals(name)) {
                return jsonObject;
            }
        }
        return null;
    }
}
