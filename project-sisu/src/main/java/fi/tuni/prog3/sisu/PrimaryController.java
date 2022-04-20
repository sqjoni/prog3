package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

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

public class PrimaryController {

    private ArrayList<JSONObject> degreeInfoObjects;

    /**
     *
     */
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
     * Creates new student object Switches to secondary view
     * 
     * @throws IOException
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

        UserData userData = new UserData(name, studentNumber, startYear, goalYear, degreeProgramName, language);

        App.setUserData(userData);

        App.setSelectionProgramInfo(programInfoObject);

        App.setRoot("secondary");
    }

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
     * Checking for mandatory information
     * 
     * @param name
     * @param studentNumber
     * @return
     */
    private boolean isValidStudentInfo(String name, String studentNumber) {
        boolean valid = true;

        String mandatoryText = "Pakollinen tieto";
        String selectProgram = "Valitse tutkinto-ohjelma";

        if (english) {
            mandatoryText = "Mandatory information";
            selectProgram = "Select degree program";
        }

        // TODO määritä parempi tarkistus sille, onko nimi ja numero valideja
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
     * Empty user input from all fields
     */
    @FXML
    private void emptyTextFields() {
        nameField.setText("");
        studentNumberField.setText("");
        startField.setText("");
        goalField.setText("");
    }

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

    private void addNameToDegreebox(JSONObject degreeNameObject) {
        String degreesName = getPreferredLanguageString(degreeNameObject);
        degreeComboBox.getItems().add(degreesName);
    }

    private void ChangeDegreeLanguage() {
        degreeComboBox.getItems().clear();
        for (JSONObject jsonObject : degreeInfoObjects) {
            JSONObject degreeNameObject = jsonObject.getJSONObject("name");
            addNameToDegreebox(degreeNameObject);
        }
    }

    private String getPreferredLanguageString(JSONObject degreeNameObject) {
        if (english && degreeNameObject.has("en")) {
            return degreeNameObject.getString("en");
        }
        if (degreeNameObject.has("fi")) {
            return degreeNameObject.getString("fi");
        }
        return degreeNameObject.getString("en");
    }

    private JSONObject getSelectionDegreeObject(String name) {
        for (JSONObject jsonObject : degreeInfoObjects) {
            JSONObject degreeNameObject = jsonObject.getJSONObject("name");
            if (getPreferredLanguageString(degreeNameObject).equals(name)) {
                return jsonObject;
            }
        }
        // Throw exception??
        return null;
    }
}
