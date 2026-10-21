package com.c0d3wan.initiativetracker.FileManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static class ConfigDTO{
        boolean darkMode;
    }

    public static void saveConfig(){
        File targetFile = new File(FileManager.getDirectory(), "Config.json");
        try{
            saveToFile(targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not save Config.json");
        }
    }

    public static void loadConfig(){
        File targetFile = new File(FileManager.getDirectory(), "Config.json");
        try{
            loadFromFile(targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not load Config.json");
        }
    }


    private static void saveToFile(File file) throws IOException{
        ConfigDTO configs = new ConfigDTO();
        //get configs

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(configs, writer);
        }
    }


    private static void loadFromFile(File file) throws IOException{
        try (FileReader reader = new FileReader(file)) {
            ConfigDTO configs = GSON.fromJson(reader, ConfigDTO.class);
            //apply configs
        }
    }






}
