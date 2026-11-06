package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.characters.*;
import com.c0d3wan.initiativetracker.characters.data.*;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.*;

import com.c0d3wan.initiativetracker.Dice;

import java.util.Map;
import java.util.Optional;

/**
 * Bridges the bestiary database records with the initiative tracker manager,
 * allowing monsters to be instantiated as non-player characters or hordes with configured HP modes and initiative rolls.
 */
public class BestiaryTrackerIntegration {
    private static final Dice DICE = new Dice();

    /**
     * Adds a monster from the bestiary into the character manager tracker with full customisation options.
     *
     * @param characterManager the active character manager instance
     * @param monsterName      the name of the monster to pull from the bestiary
     * @param customName       optional custom display name for the tracker entry
     * @param count            the quantity of creatures to add (creates a Horde if > 1)
     * @param hpMode           the health calculation mode (Average, Roll, or Manual)
     * @param manualHp         manual health value if HPMode is MANUAL
     * @param initiativeRoll   the pre-determined or manual initiative score
     * @return true if successfully added, false if the monster was not found in the database
     */
    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            String customName,
            int count,
            HPMode hpMode,
            int manualHp,
            int initiativeRoll) {

        Optional<BestiaryDTO.Monster> optionalMonster = BestiaryDatabase.findMonsterByName(monsterName);
        if (optionalMonster.isEmpty()) {
            return false;
        }

        BestiaryDTO.Monster monster = optionalMonster.get();
        String name = (customName == null || customName.isBlank()) ? monster.getName() : customName.trim();

        int dexMod = 0;
        Map<String, ?> stats = monster.getStats();
        if (stats != null && stats.containsKey("dexterity")) {
            Object dexObj = stats.get("dexterity");
            if (dexObj instanceof Number) {
                dexMod = (((Number) dexObj).intValue() - 10) / 2;
            }
        }

        int maxHealth;
        BestiaryDTO.HitPoints hpData = monster.getHitPoints();
        if (hpMode == HPMode.MANUAL) {
            maxHealth = manualHp;
        } else if (hpData != null) {
            if (hpMode == HPMode.AVERAGE) {
                maxHealth = hpData.getAverage();
            } else {
                maxHealth = DICE.rollFormula(hpData.getFormula());
            }
        } else {
            maxHealth = 10;
        }

        if (count <= 1) {
            NonePlayerCharacter npc = new NonePlayerCharacter(
                    name, initiativeRoll, dexMod, maxHealth, CharacterType.ENEMY, DeathStatus.ALIVE
            );
            npc.setEnemyType(monster.getName());
            characterManager.addCharacter(npc);
        } else {
            Horde horde = new Horde(
                    name, initiativeRoll, dexMod, maxHealth, DeathStatus.ALIVE, count
            );
            characterManager.addCharacter(horde);
        }

        return true;
    }

    /**
     * Adds a monster to the tracker with automatic initiative rolling and manual health support.
     */
    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            String customName,
            int count,
            HPMode hpMode,
            int initiativeRoll) {
        return addMonsterToTracker(characterManager, monsterName, customName, count, hpMode, 0, initiativeRoll);
    }

    /**
     * Adds a monster to the tracker with a manual health override and automatic initiative rolling.
     */
    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            int count,
            HPMode hpMode,
            int manualHp) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, null, count, hpMode, manualHp, initiativeRoll);
    }

    /**
     * Adds a monster to the tracker with standard default health mode and automatic initiative.
     */
    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            int count,
            HPMode hpMode) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, null, count, hpMode, 0, initiativeRoll);
    }

    /**
     * Adds a monster to the tracker specifying custom naming and count under standard initiative rules.
     */
    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            String customName,
            int count,
            HPMode hpMode) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, customName, count, hpMode, 0, initiativeRoll);
    }

    /**
     * Rolls an initiative score for a specific monster based on its dexterity modifier.
     *
     * @param monsterName the name of the monster in the database
     * @return the total initiative roll (d20 + Dex modifier)
     */
    private static int rollMonsterInitiative(String monsterName) {
        Optional<BestiaryDTO.Monster> optionalMonster = BestiaryDatabase.findMonsterByName(monsterName);
        int dexMod = 0;
        if (optionalMonster.isPresent()) {
            Map<String, ?> stats = optionalMonster.get().getStats();
            if (stats != null && stats.containsKey("dexterity")) {
                Object dexObj = stats.get("dexterity");
                if (dexObj instanceof Number) {
                    dexMod = (((Number) dexObj).intValue() - 10) / 2;
                }
            }
        }
        return DICE.D20() + dexMod;
    }
}