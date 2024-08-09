package ru.svinbreaker.bulbulatorLocalization.FileManagers;

import ru.svinbreaker.bulbulatorLocalization.AbstractLocalizationFileManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonLocalizationFileManager extends AbstractLocalizationFileManager {

    public JsonLocalizationFileManager() {
        super(".json");
    }

    @Override
    public String getLocalizedString(String filePath, String key) {
        String value = null;
        try {
            if (Files.exists(Paths.get(filePath))) {
                JSONObject jsonObject = getJsonObject(filePath);
                if (jsonObject != null) {
                    value = jsonObject.optString(key, null); // Returns null if key is not found
                }
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while trying to get a localized string: " + ex.getMessage());
        }
        return value;
    }

    @Override
    public void addKeyValuePair(String filePath, String key, String value) {
        try {
            JSONObject jsonObject = getJsonObject(filePath);
            if (jsonObject != null) {
                jsonObject.put(key, value);
                Files.write(Paths.get(filePath), jsonObject.toString().getBytes());
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while trying to add a key-value pair to the localization file: " + ex.getMessage());
        }
    }

    @Override
    public void removeKeyValuePair(String filePath, String key) {
        try {
            JSONObject jsonObject = getJsonObject(filePath);
            if (jsonObject != null) {
                if (!jsonObject.has(key)) {
                    throw new RuntimeException("The key does not exist in the localization file");
                }
                jsonObject.remove(key);
                Files.write(Paths.get(filePath), jsonObject.toString().getBytes());
            }
        } catch (Exception ex) {
            System.out.println("An error occurred while trying to delete a key-value pair from the localization file: " + ex.getMessage());
        }
    }

    private JSONObject getJsonObject(String filePath) {
        JSONObject jsonObject = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonObject = new JSONObject(content);
        } catch (IOException ex) {
            System.out.println("Localization file not found: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("An error occurred while trying to read the localization file: " + ex.getMessage());
        }
        return jsonObject;
    }
}
