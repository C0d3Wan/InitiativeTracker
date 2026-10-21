package com.eubi.initiativetracker.SaveLoad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class SaveLoadManager {

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
}
