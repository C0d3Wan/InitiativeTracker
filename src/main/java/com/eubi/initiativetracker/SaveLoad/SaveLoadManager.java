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

    public static void saveToFile(CharacterManager manager, File file) {
        if (!file.getName().endsWith(".json")) {
            file = new File(file.getParentFile(), file.getName() + ".json");
        }

        CombatSessionDTO session = new CombatSessionDTO();

        List<Character> activeOrder = manager.getInitiativeOrder();


    }

    public static void loadFromFile(CharacterManager manager, File file){}
}
