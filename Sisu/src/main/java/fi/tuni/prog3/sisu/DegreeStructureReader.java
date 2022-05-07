package fi.tuni.prog3.sisu;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.TreeItem;

/**
 * The degree structure reader, which reads the different modules from the data
 * received from the Sisu-API.
 */
public class DegreeStructureReader {
    private SisuReader sisuReader;
    private String primaryLanguage;
    private String secondaryLanguage;

    /**
     * Creates a new datareader and sets the preferred language and a secondary language.
     * @param language The preferred language
     */
    public DegreeStructureReader(String language) {
        this.setLanguage(language);
        if (language == "fi") {
            secondaryLanguage = "en";
        } else {
            secondaryLanguage = "fi";
        }
    }

    /**
     * Returns the preferred language.
     * @return The preferred language
     */
    public String getLanguage() {
        return primaryLanguage;
    }

    /**
     * Sets the preferred language.
     * @param language The language to set
     */
    public void setLanguage(String language) {
        this.primaryLanguage = language;
    }

    /**
     * Reads data of given degreeProgram and adds data to root.
     * @param degreeInfo The degree data received from the Sisu-API
     * @param root The root treeitem, onto which all modules and courses are added to
     * @throws MalformedURLException When the URL doesn't match anything
     */
    public void readStructure(JSONObject degreeInfo, TreeItem<String> root) throws MalformedURLException {

        sisuReader = new SisuReader();

        JSONObject degreeNameObject = degreeInfo.getJSONObject("name");
        String degreesName = getPreferredLanguageString(degreeNameObject);

        String degreeCode = degreeInfo.getString("code");
        String minDegreeCredits = degreeInfo.getJSONObject("targetCredits").get("min").toString();

        root.setValue(degreesName + " " + degreeCode + " Credits: " + minDegreeCredits);

        getChildObjectRule(degreeInfo, root);

    }

    /**
     * Recursively get the ruleObject, if no ruleObject is found get rulesArray.
     * @param jsonObject The jsonObject to be checked
     * @param parentItem The parent treeitem
     * @throws MalformedURLException When the URL doesn't match anything
     * @throws JSONException When the entered key isn't found
     */
    private void getChildObjectRule(JSONObject jsonObject, TreeItem<String> parentItem)
            throws MalformedURLException, JSONException {
        if (!jsonObject.has("rule")) {
            getChildArrayRules(jsonObject, parentItem);
            return;
        }
        JSONObject ruleObject = jsonObject.getJSONObject("rule");
        getChildObjectRule(ruleObject, parentItem);
    }

    /**
     * Recursively get the childRulesArray, if no rulesArray is found, get info of a
     * rulesObject.
     * @param jsonObject The jsonObject to be checked
     * @param parentItem The parent treeitem
     * @throws MalformedURLException When the URL doesn't match anything
     * @throws JSONException When the entered key isn't found
     */
    private void getChildArrayRules(JSONObject jsonObject, TreeItem<String> parentItem)
            throws MalformedURLException, JSONException {
        if (!jsonObject.has("rules")) {
            getRulesObjectInfo(jsonObject, parentItem);
            return;
        }
        JSONArray rulesArray = jsonObject.getJSONArray("rules");
        for (int i = 0; i < rulesArray.length(); i++) {
            getChildArrayRules(rulesArray.getJSONObject(i), parentItem);
        }
    }

    /**
     * Checks what is the rule module type, acts according to that,
     * creates a new treeitem and adds it to the parent item.
     * @param jsonObject The jsonObject to be checked
     * @param parentItem The parent treeitem
     * @throws MalformedURLException When the URL doesn't match anything
     * @throws JSONException When the entered key isn't found
     */
    private void getRulesObjectInfo(JSONObject jsonObject, TreeItem<String> parentItem)
            throws MalformedURLException, JSONException {

        String objectType = jsonObject.getString("type");

        /**
         * ModuleRule is always a study module or a so called grouping module,
         * under which are always courses, more study modules or both
         */
        if (objectType.equals("ModuleRule")) {
            JSONArray groupModule = (JSONArray) sisuReader.getModuleByGroupId(jsonObject.getString("moduleGroupId"));

            JSONObject groupModuleInfo = groupModule.getJSONObject(0);

            StringBuilder moduleInfoString = new StringBuilder();

            JSONObject groupModuleName = groupModuleInfo.getJSONObject("name");
            String name = getPreferredLanguageString(groupModuleName);
            moduleInfoString.append(name);

            if (groupModuleInfo.has("targetCredits")) {
                String groupModuleCredits = groupModuleInfo.getJSONObject("targetCredits").get("min").toString();
                moduleInfoString.append(" " + groupModuleCredits);
            }

            TreeItem<String> moduleItem = new TreeItem<>(moduleInfoString.toString());

            parentItem.getChildren().add(moduleItem);

            if (groupModuleName.has("en")) {
                if (groupModuleName.getString("en").equals("Free Choice Course Units")) {
                    return;
                }
            } else {
                if (groupModuleName.getString("fi").equals("Vapaasti valittavat opintojaksot")) {
                    return;
                }
            }
            getChildObjectRule(groupModuleInfo, moduleItem);
        }

        /**
         * CourseUnitRule always has a course under it, here a TreeItem of the
         * course is created
         */
        else if (objectType.equals("CourseUnitRule")) {
            JSONArray courseUnit = (JSONArray) sisuReader
                    .getCourseUnitByGroupId(jsonObject.getString("courseUnitGroupId"));

            JSONObject courseObject = courseUnit.getJSONObject(0);
            JSONObject courseNameObject = courseObject.getJSONObject("name");
            String courseName = getPreferredLanguageString(courseNameObject);
            String courseCredits = courseObject.getJSONObject("credits").get("min").toString();
            String courseCode = courseObject.getString("code");

            TreeItem<String> courseUnitItem = new TreeItem<>(
                    courseName + " " + courseCode + " Credits: " + courseCredits);
            parentItem.getChildren().add(courseUnitItem);
        }

        /**
         * AnyModuleRule can contain any study module under it
         */
        else if (objectType.equals("AnyModuleRule")) {
            return;
        }

        else if (objectType.equals("CreditsRule")) {
            String ruleType = jsonObject.getString("type");
            String ruleCredits = jsonObject.getJSONObject("credits").get("min").toString();
            String description = "";
            if (jsonObject.getJSONObject("rule").get("description") instanceof JSONObject) {
                JSONObject desc = jsonObject.getJSONObject("rule").getJSONObject("description");
                description = getPreferredLanguageString(desc);
            }

            TreeItem<String> creditRuleItem = new TreeItem<>(ruleType + " " + ruleCredits + " \n" + description);
            parentItem.getChildren().add(creditRuleItem);
            getChildObjectRule(jsonObject, creditRuleItem);
        }
    }

    /**
     * @param jsonObject The degree module to be checked
     * @return The string representation of the module in the correct language
     */
    private String getPreferredLanguageString(JSONObject jsonObject) {
        if (jsonObject.has(primaryLanguage)) {
            return jsonObject.getString(primaryLanguage);
        } else if (jsonObject.has(secondaryLanguage)) {
            return jsonObject.getString(secondaryLanguage);
        }
        return "";
    }
}
