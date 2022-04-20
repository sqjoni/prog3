package fi.tuni.prog3.sisu;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.TreeItem;

// Melkoinen sekoiluhan tämä on edelleen
// Hakee ny kaikkee kivaa ja puskee näytölle
// Erittäin hidas
public class DegreeStructureReader {
    private SisuReader sisuReader;
    private String primaryLanguage;
    private String secondaryLanguage;

    /**
     * Creates new datareader and sets preferred language And secondary language
     * 
     * @param language preferred language
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
     * @return the language
     */
    public String getLanguage() {
        return primaryLanguage;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.primaryLanguage = language;
    }

    // Reads data of given degreeProgram and adds data to root
    public void readStructure(JSONObject degreeInfo, TreeItem<String> root) throws MalformedURLException {

        sisuReader = new SisuReader();

        JSONObject degreeNameObject = degreeInfo.getJSONObject("name");
        String degreesName = getPreferredLanguageString(degreeNameObject);

        String degreeCode = degreeInfo.getString("code");
        String minDegreeCredits = degreeInfo.getJSONObject("targetCredits").get("min").toString();

        root.setValue(degreesName + " " + degreeCode + " Credits: " + minDegreeCredits);

        getChildObjectRule(degreeInfo, root);

    }

    // Recursively get the ruleObject, if no ruleObject is found get rulesArray
    private void getChildObjectRule(JSONObject jsonObject, TreeItem<String> parentItem)
            throws MalformedURLException, JSONException {
        if (!jsonObject.has("rule")) {
            getChildArrayRules(jsonObject, parentItem);
            return;
        }
        JSONObject ruleObject = jsonObject.getJSONObject("rule");
        getChildObjectRule(ruleObject, parentItem);
    }

    // Recursively get the childRulesArray, if no rulesArray is found, get info of a
    // rulesObject
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

    // Checks what is the rule module type, acts according to that,
    // Creates new treeitem and adds it to the parent item

    // TODO jaa muutamaan osaan, poista turhat toistot

    private void getRulesObjectInfo(JSONObject jsonObject, TreeItem<String> parentItem)
            throws MalformedURLException, JSONException {

        String objectType = jsonObject.getString("type");

        // Module rule on aina joko opintokokonaisuus, tai ns grouping module,
        // jonka alla on opintokokonaisuuksia, kursseja tai molempia
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

            // Jos kyseessä ei ole vapaasti valittavat opintojaksot, haetaan opintojakson
            // kurssit
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

        // CourseUnitRulen alla on aina kurssi, kurssista tulisi vetää tiedot ulos
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

        // AnyModuleRulen alla voi olla mikä tahansa opintokokonaisuus
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

    private String getPreferredLanguageString(JSONObject jsonObject) {
        if (jsonObject.has(primaryLanguage)) {
            return jsonObject.getString(primaryLanguage);
        } else if (jsonObject.has(secondaryLanguage)) {
            return jsonObject.getString(secondaryLanguage);
        }
        return "";
    }
}
