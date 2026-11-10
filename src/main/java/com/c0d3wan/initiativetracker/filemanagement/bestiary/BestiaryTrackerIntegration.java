package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
import com.c0d3wan.initiativetracker.characters.data.CharacterType;
import com.c0d3wan.initiativetracker.characters.data.DeathStatus;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.HPMode;

public class BestiaryTrackerIntegration {

    public static boolean addMonsterToTracker(CharacterManager characterManager, String monsterName, String customName, int count, HPMode hpMode, int manualHp) {
        return addMonsterToTracker(characterManager, monsterName, customName, count, hpMode, manualHp, null);
    }

    public static boolean addMonsterToTracker(CharacterManager characterManager, String monsterName, String customName, int count, HPMode hpMode, int manualHp, Integer manualInit) {
        BestiaryDTO.Monster monster = BestiaryDatabase.findMonsterByName(monsterName).orElse(null);
        if (monster == null || characterManager == null) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            int assignedHp = switch (hpMode) {
                case AVERAGE -> monster.getHitPoints() != null ? monster.getHitPoints().getAverage() : 10;
                case ROLL -> evaluateHpFormula(monster.getHitPoints() != null ? monster.getHitPoints().getFormula() : "2d8");
                case MAX -> calculateMaxHp(monster.getHitPoints() != null ? monster.getHitPoints().getFormula() : "2d8");
                case CUSTOM -> manualHp;
            };

            String baseName = (customName == null || customName.isBlank()) ? monster.getName() : customName.trim();
            String combatantName = (count > 1) ? baseName + " " + (i + 1) : baseName;

            int dexScore = 10;
            if (monster.getStats() != null && monster.getStats().containsKey("dexterity")) {
                dexScore = monster.getStats().get("dexterity");
            }
            int dexMod = (dexScore - 10) / 2;

            int initiativeValue = manualInit != null ? manualInit : ((int) (Math.random() * 20) + 1 + dexMod);

            NonePlayerCharacter npc = new NonePlayerCharacter(
                    combatantName,
                    initiativeValue,
                    dexScore,
                    assignedHp,
                    CharacterType.ENEMY,
                    DeathStatus.ALIVE
            );

            characterManager.addCharacter(npc);
        }

        return true;
    }

    private static int evaluateHpFormula(String formula) {
        try {
            if (formula == null || !formula.contains("d")) return 10;
            String[] parts = formula.split("\\+");
            String[] diceParts = parts[0].trim().split("d");
            int count = Integer.parseInt(diceParts[0]);
            int sides = Integer.parseInt(diceParts[1]);
            int modifier = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            return (count * ((sides + 1) / 2)) + modifier;
        } catch (Exception e) {
            return 10;
        }
    }

    private static int calculateMaxHp(String formula) {
        try {
            if (formula == null || !formula.contains("d")) return 15;
            String[] parts = formula.split("\\+");
            String[] diceParts = parts[0].trim().split("d");
            int count = Integer.parseInt(diceParts[0]);
            int sides = Integer.parseInt(diceParts[1]);
            int modifier = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            return (count * sides) + modifier;
        } catch (Exception e) {
            return 15;
        }
    }
}