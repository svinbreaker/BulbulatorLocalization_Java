package ru.svinbreaker.bulbulatorLocalization.FileManagers;

import ru.svinbreaker.bulbulatorLocalization.AbstractLocalizationFileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class TxtLocalizationFileManager extends AbstractLocalizationFileManager {

    public TxtLocalizationFileManager() {
        super(".txt");
    }

    @Override
    public String getLocalizedString(String filePath, String key) {
        String value = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (String line : lines) {
                String[] parts = line.split(":");

                if (parts.length == 2) {
                    String currentKey = parts[0].trim();
                    String currentValue = parts[1].trim();

                    if (currentKey.equalsIgnoreCase(key)) {
                        value = currentValue;
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("An error occurred while trying to get a localized string: " + ex.getMessage());
        }
        return value;
    }

    @Override
    public void addKeyValuePair(String filePath, String key, String value) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length > 0 && parts[0].trim().equals(key)) {
                    throw new IllegalArgumentException("This key is already used in the localization file");
                }
            }

            String keyValueString = key + ": " + value;
            Files.write(Paths.get(filePath), (keyValueString + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.APPEND);

        } catch (IOException ex) {
            System.out.println("An error occurred while trying to add a key-value pair to the localization file: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void removeKeyValuePair(String filePath, String key) {
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
            boolean keyExist = false;

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(":");
                if (parts.length > 0 && parts[0].trim().equals(key)) {
                    lines.remove(i);
                    keyExist = true;
                    break;
                }
            }

            if (!keyExist) {
                throw new IllegalArgumentException("The key does not exist in the localization file");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException ex) {
            System.out.println("An error occurred while trying to delete a key-value pair from the localization file: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private List<String> getFileLines(String filePath) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException ex) {
            System.out.println("The localization file not found: " + ex.getMessage());
        }
        return lines;
    }
}