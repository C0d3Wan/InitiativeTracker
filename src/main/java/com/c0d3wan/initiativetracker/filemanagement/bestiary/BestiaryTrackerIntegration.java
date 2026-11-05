package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.characters.*;
import com.c0d3wan.initiativetracker.characters.data.*;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.*;

import com.c0d3wan.initiativetracker.Dice;

import java.util.Map;
import java.util.Optional;

public class BestiaryTrackerIntegration {
    private static final Dice DICE = new Dice();

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

    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            String customName,
            int count,
            HPMode hpMode,
            int initiativeRoll) {
        return addMonsterToTracker(characterManager, monsterName, customName, count, hpMode, 0, initiativeRoll);
    }

    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            int count,
            HPMode hpMode,
            int manualHp) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, null, count, hpMode, manualHp, initiativeRoll);
    }

    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            int count,
            HPMode hpMode) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, null, count, hpMode, 0, initiativeRoll);
    }

    public static boolean addMonsterToTracker(
            CharacterManager characterManager,
            String monsterName,
            String customName,
            int count,
            HPMode hpMode) {
        int initiativeRoll = rollMonsterInitiative(monsterName);
        return addMonsterToTracker(characterManager, monsterName, customName, count, hpMode, 0, initiativeRoll);
    }

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