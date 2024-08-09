package ru.svinbreaker.bulbulatorLocalization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Localizer {

    private List<AbstractLocalizationFileManager> localizationFileManagers = new ArrayList<>();
    private Map<String, String> languagesPaths = new HashMap<>();

    public Localizer() {
        addDefaultLocalizationFileManagers();
    }

    public Localizer(List<String> filePaths) {
        addDefaultLocalizationFileManagers();
        for (String filePath : filePaths) {
            addLocalizationFile(filePath);
        }
    }

    public String getLocalizedString(String languageCode, String key) {
        String filePath = languagesPaths.get(languageCode);
        String extension = getExtension(filePath);

        AbstractLocalizationFileManager localizationFileManager = getLocalizationFileManagerByExtension(extension);
        if (localizationFileManager == null) {
            throw new UnsupportedOperationException("Unsupported file format: " + extension);
        }

        return localizationFileManager.getLocalizedString(filePath, key);
    }

    public void createLocalizationFile(String filePath, String languageCode, String extension) {
        if (!extensionIsSupported(extension)) {
            throw new UnsupportedOperationException("Unsupported file format: " + extension);
        }

        if (languagesPaths.containsKey(languageCode)) {
            throw new IllegalArgumentException("Language file already exists: " + languageCode + extension);
        }

        try {
            File file = new File(filePath, languageCode + "." + extension);
            File directory = new File(filePath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            addLocalizationFile(file.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("An error occurred while creating the localization file: " + ex.getMessage());
        }
    }

    public void deleteLocalizationFile(String languageCode) {
        try {
            removeLocalizationFile(languageCode);
            Files.deleteIfExists(Paths.get(languagesPaths.get(languageCode)));
        } catch (IOException ex) {
            System.out.println("An error occurred while deleting the localization file: " + ex.getMessage());
        }
    }

    public void addLocalizationFile(String filePath) {
        try {
            languagesPaths.put(getFileNameWithoutExtension(filePath), filePath);
        } catch (Exception ex) {
            System.out.println("An error occurred while adding the localization file: " + ex.getMessage());
        }
    }

    public void addKeyValuePair(String languageCode, String key, String value) {
        String filePath = languagesPaths.get(languageCode);
        String extension = getExtension(filePath);

        AbstractLocalizationFileManager localizationFileManager = getLocalizationFileManagerByExtension(extension);
        if (localizationFileManager == null) {
            throw new UnsupportedOperationException("Unsupported file format: " + extension);
        }

        localizationFileManager.addKeyValuePair(filePath, key, value);
    }

    public void removeKeyValuePair(String languageCode, String key) {
        String filePath = languagesPaths.get(languageCode);
        String extension = getExtension(filePath);

        AbstractLocalizationFileManager localizationFileManager = getLocalizationFileManagerByExtension(extension);
        if (localizationFileManager == null) {
            throw new UnsupportedOperationException("Unsupported file format: " + extension);
        }

        localizationFileManager.removeKeyValuePair(filePath, key);
    }

    public void removeLocalizationFile(String languageCode) {
        try {
            languagesPaths.remove(languageCode);
        } catch (Exception ex) {
            System.out.println("An error occurred while removing the localization file: " + ex.getMessage());
        }
    }

    private boolean extensionIsSupported(String extension) {
        return localizationFileManagers.stream()
                .anyMatch(manager -> manager.getExtension().equals(extension));
    }

    private AbstractLocalizationFileManager getLocalizationFileManagerByExtension(String extension) {
        return localizationFileManagers.stream()
                .filter(manager -> manager.getExtension().equals(extension))
                .findFirst()
                .orElse(null);
    }

    private void addDefaultLocalizationFileManagers() {
        ServiceLoader<AbstractLocalizationFileManager> loader = ServiceLoader.load(AbstractLocalizationFileManager.class);
        for (AbstractLocalizationFileManager manager : loader) {
            localizationFileManagers.add(manager);
        }
    }

    public void addLocalizationFileManager(AbstractLocalizationFileManager localizationFileManager) {
        try {
            localizationFileManagers.add(localizationFileManager);
        } catch (Exception ex) {
            System.out.println("An error occurred while adding a localization file manager: " + ex.getMessage());
        }
    }

    public void removeLocalizationFileManager(AbstractLocalizationFileManager localizationFileManager) {
        try {
            localizationFileManagers.remove(localizationFileManager);
        } catch (Exception ex) {
            System.out.println("An error occurred while removing a localization file manager: " + ex.getMessage());
        }
    }

    public boolean containsLanguage(String languageCode) {
        return languagesPaths.containsKey(languageCode);
    }

    public List<String> getLanguages() {
        return new ArrayList<>(languagesPaths.keySet());
    }

    // Utility methods to handle file paths

    private String getExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf('.') + 1);
    }

    private String getFileNameWithoutExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf('.'));
    }
}
