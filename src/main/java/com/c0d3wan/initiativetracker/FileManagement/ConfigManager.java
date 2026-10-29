package com.c0d3wan.initiativetracker.FileManagement;

import com.c0d3wan.initiativetracker.swingui.UIManager;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.formdev.flatlaf.*;

/**
 * Manages the serialization and deserialization of application configuration settings,
 * such as UI preferences and display options, stored in a JSON configuration file.
 */
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Data transfer object representing the persistent configuration options.
     */
    private static class ConfigDTO {
        boolean darkMode;
    }

    /**
     * Saves the current application settings to {@code Config.json} in the configuration directory.
     */
    public static void saveConfig() {
        File targetFile = new File(FileManager.getDirectory(), "Config.json");
        try {
            saveToFile(targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not save Config.json");
        }
    }

    /**
     * Loads application settings from {@code Config.json} in the configuration directory
     * and applies them to the user interface.
     */
    public static void loadConfig() {
        File targetFile = new File(FileManager.getDirectory(), "Config.json");
        if (!targetFile.exists()) {
            return;
        }

        try {
            loadFromFile(targetFile);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Warning: Could not load Config.json");
        }
    }

    /**
     * Serializes current UI settings into JSON and writes them to the specified file.
     *
     * @param file The destination file.
     * @throws IOException If writing to the file fails.
     */
    private static void saveToFile(File file) throws IOException {
        ConfigDTO configs = new ConfigDTO();
        configs.darkMode = FlatLaf.isLafDark();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(configs, writer);
        }
    }

    /**
     * Reads and parses configuration JSON from disk and applies theme settings.
     *
     * @param file The configuration file to read.
     * @throws IOException If reading the file fails.
     */
    private static void loadFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            ConfigDTO configs = GSON.fromJson(reader, ConfigDTO.class);

            if (configs != null) {
                UIManager.setDarkMode(configs.darkMode);
            }
        }
    }
}