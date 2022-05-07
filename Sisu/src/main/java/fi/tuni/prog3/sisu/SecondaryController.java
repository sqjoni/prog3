package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * The controller for the second window. Creates the UI components.
 */
public class SecondaryController {
    @FXML
    GridPane mainGridPane;

    @FXML
    Button backButton;

    /**
     * Changes the window back to the primary window, saves userData aswell.
     * @throws IOException When the file cannot be located
     */
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
        App.saveUserData();
    }

    /**
     * Adds student information to the window.
     * @param userData Object containing the students information
     * @throws MalformedURLException When the URL doesn't match anything
     */
    public void addDegreeInfo(UserData userData) throws MalformedURLException {
        Label studentInfo = new Label(userData.toString());
        studentInfo.setAlignment(Pos.CENTER_LEFT);
        mainGridPane.add(studentInfo, 1, 0);
    }

    /**
     * Adds the treeview to the scrollpane given via a parameter.
     * Works by reading the degrees json structure and adds the proper
     * parts to the proper treeitems. Also sets a mouse click -event to
     * the treeitems.
     * @param spCourses The scrollpane to be added to
     * @param complCourses The scrollpane into which the double-clicked courses
     * are added to or removed from
     * @param languageCode The language with which the degree should be unpacked
     * with
     * @throws MalformedURLException When the URL doesn't match anything
     */
    public void addTreeView(ScrollPane spCourses, ScrollPane complCourses, String languageCode)
            throws MalformedURLException {
        TreeItem<String> root = new TreeItem<>();

        // Get info from the Sisu-API and add to root
        DegreeStructureReader reader = new DegreeStructureReader(languageCode);

        reader.readStructure(App.getSelectionProgramInfo(), root);

        root.setExpanded(false);
        TreeView<String> tv = new TreeView<>(root);

        // Add functionality to the treeitems
        tv.setCellFactory(tree -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    if (!cell.isEmpty()) {
                        TreeItem<String> treeItem = cell.getTreeItem();
                        if (treeItem.isLeaf()) {
                            String textValue = treeItem.getValue();
                            if (!textValue.equals("Vapaasti valittavat opintojaksot 0")
                                    && !textValue.equals("Vapaasti valittava opintokokonaisuus")) {
                                App.getStudent().updateCourses(textValue);
                                updateCoursePane(complCourses);
                            }
                        }
                    }
                }
            });
            return cell;
        });
        spCourses.setContent(tv);
    }

    /**
     * A recursive function with which to set the completed courses in a
     * reverse order onto the vbox.
     * @param courses A list of the students completed courses
     * @param vbox The vbox onto which the course labels are added
     * @param idx The index telling which element to add to the vbox
     */
    public void recursiveUpdateCoursePane(List<String> courses, VBox vbox, int idx, ScrollPane complCourses) {
        if (idx < courses.size()) {
            idx++;
            recursiveUpdateCoursePane(courses, vbox, idx, complCourses);
            Label comp = new Label(courses.get(idx-1));
            comp.setId("completedCourse");

            comp.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    String textValue = comp.getText();
                    if (!textValue.equals("Vapaasti valittavat opintojaksot 0")
                            && !textValue.equals("Vapaasti valittava opintokokonaisuus")) {
                        App.getStudent().updateCourses(textValue);
                        updateCoursePane(complCourses);
                    }
                }
            });

            vbox.getChildren().add(comp);
        }
    }

    /**
     * Updates the window containing the completed courses.
     * @param complCourses The scrollpane containing the completed courses
     */
    public void updateCoursePane(ScrollPane complCourses) {
        VBox vbox = new VBox();
        var user = App.getStudent();
        var courses = user.getCourses();
        recursiveUpdateCoursePane(courses, vbox, 0, complCourses);
        complCourses.setContent(vbox);
    }

    /**
     * Builds the scrollpanes to the UI and calls for two functions:
     * addTreeView to add the treeview of the courses on the left and
     * updateCoursePane to add the completed courses to the right.
     * @param userData Object containing the students information
     * @throws MalformedURLException When the URL doesn't match anything
     */
    public void createUI(UserData userData) throws MalformedURLException {
        ScrollPane spCourses = new ScrollPane();
        spCourses.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spCourses.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spCourses.setFitToWidth(true);
        spCourses.setFitToHeight(true);
        mainGridPane.add(spCourses, 0, 1);

        ScrollPane complCourses = new ScrollPane();
        complCourses.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        complCourses.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        complCourses.setFitToWidth(true);
        complCourses.setFitToHeight(true);
        mainGridPane.add(complCourses, 1, 1);

        String languageCode = userData.getLanguage();

        if (languageCode.equals("en")) {
            backButton.setText("Back");
        }

        addTreeView(spCourses, complCourses, languageCode);
        updateCoursePane(complCourses);
    }
}
