package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import components.AppFileComponent;
import controller.HangmanController;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ritwik Banerjee
 */
public class GameDataFile implements AppFileComponent {

    public static final String TARGET_WORD  = "TARGET_WORD";
    public static final String GOOD_GUESSES = "GOOD_GUESSES";
    public static final String BAD_GUESSES  = "BAD_GUESSES";
    public static final String REMAINING_GUESSES  = "REMAINING_GUESSES";
    public static final String CHARACTER = "CHARACTER";

    @Override
    public void saveData(AppDataComponent data, Path to) throws FileNotFoundException {
        GameData dataManager = (GameData) data;
        String target = dataManager.getTargetWord();
        JsonArray good = makeJsonSetObjectG(dataManager);
        JsonArray bad = makeJsonSetObjectB(dataManager);
        int rem = dataManager.getRemainingGuesses();

        JsonObject saveJson = Json.createObjectBuilder()
                .add(TARGET_WORD, target)
                .add(REMAINING_GUESSES, rem)
                .add(GOOD_GUESSES, good)
                .add(BAD_GUESSES, bad).build();

        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(saveJson);
        jsonWriter.close();

        // INIT THE WRITER
        OutputStream os;
        if (to.toString().endsWith(".json")) {
            os = new FileOutputStream(to.toString());
        }
        else {
            os = new FileOutputStream(to.toString() + ".json");
        }
        JsonWriter jsonFileWriter = Json.createWriter(os);
        jsonFileWriter.writeObject(saveJson);
        String prettyPrinted = sw.toString();
        PrintWriter pw;
        if (to.toString().endsWith(".json")) {
            pw = new PrintWriter(to.toString());
        }
        else {
            pw = new PrintWriter(to.toString() + ".json");
        }
        pw.write(prettyPrinted);
        pw.close();
    }

    public JsonArray makeJsonSetObjectG (GameData dataManager) {
        Set<Character> chars = dataManager.getGoodGuesses();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Character c : chars) {
            JsonObject charJson = Json.createObjectBuilder()
                    .add(CHARACTER, c).build();
            arrayBuilder.add(charJson);
        }
        JsonArray charArray = arrayBuilder.build();
        return charArray;
    }
    public JsonArray makeJsonSetObjectB (GameData dataManager) {
        Set<Character> chars = dataManager.getBadGuesses();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Character c : chars) {
            JsonObject charJson = Json.createObjectBuilder()
                    .add(CHARACTER, c).build();
            arrayBuilder.add(charJson);
        }
        JsonArray charArray = arrayBuilder.build();
        return charArray;
    }


    @Override
    public void loadData(AppDataComponent data, String from) throws IOException {

        GameData dataManager = (GameData) data;

        JsonObject json = loadJSONFile(from);

        JsonString jsonString = json.getJsonString(TARGET_WORD);
        String tWord = jsonString.getString();
        dataManager.setTargetWord(tWord);

        int guessLeft = json.getJsonNumber(REMAINING_GUESSES).intValue();
        dataManager.setRemainingGuesses(guessLeft);

        JsonArray jsonGoodArray = json.getJsonArray(GOOD_GUESSES);
        JsonArray jsonBadArray = json.getJsonArray(BAD_GUESSES);
        Set<Character> goodG = new HashSet<>();
        Set<Character> badG = new HashSet<>();
        for (int i = 0; i < jsonGoodArray.size(); i++) {
            JsonObject tempJson = jsonGoodArray.getJsonObject(i);
            char guess = (char) tempJson.getJsonNumber(CHARACTER).intValue();
            dataManager.addGoodGuess(guess);
            goodG.add(guess);
        }
        for (int i = 0; i < jsonBadArray.size(); i++) {
            JsonObject tempJson = jsonBadArray.getJsonObject(i);
            char guess = (char) tempJson.getJsonNumber(CHARACTER).intValue();
            dataManager.addBadGuess(guess);
            badG.add(guess);
        }
        guessLeft = json.getJsonNumber(REMAINING_GUESSES).intValue();
        dataManager.setRemainingGuesses(guessLeft);

    }

    public char loadChar (JsonObject json) {
        String jsChar = json.getString(CHARACTER);
        char finalChar = jsChar.toCharArray()[0];
        return finalChar;
    }

    public int getDataAsInt(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonNumber number = (JsonNumber) value;
        return number.intValue();
    }
    public String getDataAsString(JsonObject json, String dataName) {
        JsonValue value = json.get(dataName);
        JsonString string = (JsonString) value;
        return string.getString();
    }

    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }

    /** This method will be used if we need to export data into other formats. */
    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException { }
}
