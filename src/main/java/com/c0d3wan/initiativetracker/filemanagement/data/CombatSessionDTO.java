package com.c0d3wan.initiativetracker.filemanagement.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object representing the top-level structure of a saved combat session.
 */
public class CombatSessionDTO {
    public String activeCharacterName;
    public List<CharacterRecordDTO> combatants = new ArrayList<>();

    /**
     * Data transfer object representing a serialized combatant, including stats,
     * death saves, NPC flags, and child horde members.
     */
    public static class CharacterRecordDTO {
        public String name;
        public int initiative;
        public int dexterity;
        public String type;
        public String deathStatus;
        public int passSaves;
        public int failSaves;
        public boolean isNPC;
        public boolean isHorde;
        public List<NPCRecordDTO> npcs = new ArrayList<>();
        public int currentHealth;
        public int maxHealth;
        public String enemyType;
    }

    /**
     * Data transfer object representing an individual NPC within a horde.
     */
    public static class NPCRecordDTO {
        public String name;
        public int initiative;
        public int dexterity;
        public String type;
        public String deathStatus;
        public int currentHealth;
        public int maxHealth;
        public String enemyType;
    }
}