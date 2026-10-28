package com.c0d3wan.initiativetracker.characters;

import java.util.*;

/**
 * Represents a combatant entity within the initiative tracking system.
 * <p>
 * This class encapsulates state data for an individual participant, including their
 * unique display name, rolled initiative, tie-breaker dexterity modifier, alignment/type,
 * death status, and death saving throw tally. Dexterity is converted into a two-decimal
 * fractional modifier to automatically resolve ties in turn ordering.
 */
public class Character {
    private final String characterName;
    private int baseInitiative;
    private int dexterity;
    private final Map<String, Integer> deathSaves;
    private final CharacterType characterType;
    private DeathStatus deathStatus;


    public Character(String name, int initiative, int dex, CharacterType type, DeathStatus status) {
        characterName = name;
        characterType = type;
        deathStatus = status;

        dexterity = (characterType == CharacterType.ENEMY) ? 0 : dex;
        baseInitiative = initiative;

        deathSaves = new HashMap<>();
        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
    }


    public Character(String name, int initiative, int dex, String typeID, String deathID) {
        this(name, initiative, dex, typeFromString(typeID), deathFromString(deathID));
    }


    public static CharacterType typeFromString(String id) {
        try{
            return CharacterType.valueOf(id.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException | NullPointerException e){
            return CharacterType.UNKNOWN;
        }
    }

    public static DeathStatus deathFromString(String id) {
        try{
            return DeathStatus.valueOf(id.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException | NullPointerException e){
            return DeathStatus.UNKNOWN;
        }
    }


    public CharacterData getCharacterData() {
        return new CharacterData(
                characterName,
                baseInitiative,
                dexterity,
                getInitiativeValue(),
                characterType,
                deathStatus);
    }

    public void setInitiative(int initiative) {
        baseInitiative = initiative;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = (characterType == CharacterType.ENEMY) ? 0 : dexterity;
    }

    public void setDeathSaves(int passes, int fails) {
        deathSaves.put("Pass", passes);
        deathSaves.put("Fail", fails);
    }

    public void deathSave(boolean pass) {
        if (deathStatus != DeathStatus.DOWN) {
            return;
        }

        String key = pass ? "Pass" : "Fail";

        deathSaves.put(key, deathSaves.get(key) + 1);

        if (deathSaves.get("Pass") >= 3) {
            deathStatus = DeathStatus.ALIVE;
            resetDeathSaves();
        }

        if (deathSaves.get("Fail") >= 3) {
            deathStatus = DeathStatus.DEAD;
            resetDeathSaves();
        }
    }

    public void resetDeathSaves() {
        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
    }

    public void setDeathStatus(DeathStatus newDeathStatus) {
        deathStatus = newDeathStatus;
        if (newDeathStatus != DeathStatus.DOWN) {
            resetDeathSaves();
        }
    }

    public void setDeathStatus(String statusID) {
        setDeathStatus(deathFromString(statusID));
    }

    public float getInitiativeValue() {
        return baseInitiative + (characterType == CharacterType.ENEMY ? 0 : dexterity / 100f);
    }

    public String getName() {
        return characterName;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public DeathStatus getDeathStatus() {
        return deathStatus;
    }

    public int getBaseInitiative() {
        return baseInitiative;
    }

    public int getDexterity() {
        return dexterity;
    }

    public Map<String, Integer> getDeathSaves() {
        return Collections.unmodifiableMap(deathSaves);
    }
}