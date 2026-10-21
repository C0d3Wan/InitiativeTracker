package com.eubi.initiativetracker.SaveLoad;

import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.NonePlayerCharacter;
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

    /**
     *
     * @param manager The active CharacterManager instance.
     * @param fileName The name of the save file to make.
     */
    public static void save(CharacterManager manager, String fileName) {
        File targetFile = new File(getSaveDirectory(), fileName);
        try{
            saveToFile(manager, targetFile);
        } catch (IOException e) {
            System.err.println("Warning: Could not create save");
        }
    }

    /**
     *
     * @param manager The active CharacterManager instance.
     * @param fileName The name of the save file to load.
     */
    public static void load(CharacterManager manager, String fileName) {
        File targetFile = new File(getSaveDirectory(), fileName);
        loadFromFile(manager, targetFile);
    }

    private static void saveToFile(CharacterManager manager, File file) throws IOException {
        if (!file.getName().endsWith(".json")) {
            file = new File(file.getParentFile(), file.getName() + ".json");
        }
        System.out.println("Saving " + file.getAbsolutePath());

        CombatSessionDTO session = new CombatSessionDTO();

        List<Character> activeOrder = manager.getInitiativeOrder();
        if (!activeOrder.isEmpty()) {
            session.activeCharacterName = activeOrder.getFirst().getCharacterData().get("name").toString();
        }

        for (Character character : manager.getCharacterList()){
            Map<String, Object> data = character.getCharacterData();
            Map<String, Integer> saves = character.getDeathSaves();

            CharacterRecordDTO dto = new CharacterRecordDTO();
            dto.name = data.get("Name").toString();
            dto.initiative = (int) data.get("Initiative");
            dto.dexterity = (int) data.get("Dexterity");
            dto.type = data.get("Type").toString();
            dto.deathStatus = data.get("DeathStatus").toString();
            dto.passSaves = saves.getOrDefault("Pass", 0);
            dto.failSaves = saves.getOrDefault("Fail", 0);

            if (character instanceof NonePlayerCharacter npc) {
                dto.isNPC = true;
                dto.currentHealth = npc.getHealth();
                dto.maxHealth = npc.getMaxHealth();
                dto.enemyType = npc.getEnemyType();
            } else {
                dto.isNPC = false;
                dto.enemyType = "";
            }

            session .combatants.add(dto);
        }

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(session, writer);
        }

    }

    private static void loadFromFile(CharacterManager manager, File file){}
}
