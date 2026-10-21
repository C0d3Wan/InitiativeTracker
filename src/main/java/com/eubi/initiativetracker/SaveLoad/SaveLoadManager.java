package com.eubi.initiativetracker.SaveLoad;

import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.Character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class SaveLoadManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    /**
     * Data structure representing the entire saved encounter file.
     */
    private static class CombatSessionDTO {
        String activeCharacterName;
        List<CharacterRecordDTO> combatants = new ArrayList<>();
    }

    /**
     * Flat data structure representing a single Character or NonePlayerCharacter.
     */
    private static class CharacterRecordDTO{
        String name;
        int initiative;
        int dexterity;
        String type;
        String deathStatus;
        int passSaves;
        int failSaves;
        boolean isNPC;
        int currentHealth;
        int maxHealth;
        String enemyType;
    }


    public static boolean isPackaged() {
        try {
            String path = SaveLoadManager.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            return path != null && path.endsWith(".jar");
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Returns a local "./saves" folder when running in IntelliJ, or
     * "~/InitiativeTrackerSaves" in the user's home folder when packaged as a JAR.
     */
    public static File getSaveDirectory() {
        File saveDir;

        if (isPackaged()) {
            // Packaged release -> save in ~/InitiativeTrackerSaves
            saveDir = new File(System.getProperty("user.home"), "InitiativeTrackerSaves");
        } else {
            // Running in IntelliJ -> save in ./saves inside the project folder
            saveDir = new File("saves");
        }

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            System.err.println("Warning: Could not create save directory: " + saveDir.getAbsolutePath());
        }

        return saveDir;
    }


    public static void save(CharacterManager manager, String fileName) {
        File targetFile = new File(getSaveDirectory(), fileName);
        saveToFile(manager, targetFile);
    }

    public static void load(CharacterManager manager, String fileName) {
        File targetFile = new File(getSaveDirectory(), fileName);
        loadFromFile(manager, targetFile);
    }

    public static void saveToFile(CharacterManager manager, File file) {
        if (!file.getName().endsWith(".json")) {
            file = new File(file.getParentFile(), file.getName() + ".json");
        }
        System.out.println("Saving " + file.getAbsolutePath());

        CombatSessionDTO session = new CombatSessionDTO();

        List<Character> activeOrder = manager.getInitiativeOrder();
        if (!activeOrder.isEmpty()) {
            session.activeCharacterName = activeOrder.getFirst().getCharacterData().get("name").toString();
        }





    }

    public static void loadFromFile(CharacterManager manager, File file){}
}
