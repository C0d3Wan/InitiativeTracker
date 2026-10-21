package com.c0d3wan.initiativetracker.FileManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages the serialization and deserialization of application configuration settings,
 * such as UI preferences and display options, stored in a JSON configuration file.
 */
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Data Transfer Object (DTO) representing persistent application configuration settings.
     */
    private static class ConfigDTO {
        boolean darkMode;
    }

    /**
     * Saves the current application configuration states to disk as {@code Config.json}
     * within the designated configuration directory.
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
     * Loads application configuration states from {@code Config.json} located
     * within the designated configuration directory and applies them.
     */
    public static void loadConfig() {
        File targetFile = new File(FileManager.getDirectory(), "Config.json");
        try {
            loadFromFile(targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not load Config.json");
        }
    }


    /**
     * Internal helper method to build the configuration DTO, serialize it to JSON,
     * and write it to the target file.
     *
     * @param file The destination {@link File} for the configuration data.
     * @throws IOException if writing to the file fails.
     */
    private static void saveToFile(File file) throws IOException {
        ConfigDTO configs = new ConfigDTO();
        //get configs

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(configs, writer);
        }
    }


    /**
     * Internal helper method to read the configuration JSON file from disk,
     * parse it into a {@link ConfigDTO}, and apply the settings.
     *
     * @param file The source {@link File} containing configuration data.
     * @throws IOException if reading or parsing the file fails.
     */
    private static void loadFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            ConfigDTO configs = GSON.fromJson(reader, ConfigDTO.class);
            //apply configs
        }
    }
}