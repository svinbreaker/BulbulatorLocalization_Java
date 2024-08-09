package ru.svinbreaker.bulbulatorLocalization;

public abstract class AbstractLocalizationFileManager {
    private final String extension;

    public AbstractLocalizationFileManager(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public abstract String getLocalizedString(String filePath, String key);

    public abstract void addKeyValuePair(String filePath, String key, String value);

    public abstract void removeKeyValuePair(String filePath, String key);
}