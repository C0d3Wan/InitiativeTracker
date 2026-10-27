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
    private float baseInitiative;
    private float initiativeValue;
    private float dexValue;
    private final Map<String, Integer> deathSaves;
    private final CharacterType characterType;
    private DeathStatus deathStatus;

    /**
     * Constructs a new Character instance with fully qualified types.
     * <p>
     * The final {@code initiativeValue} is calculated as {@code baseInitiative + (dex / 100.0f)}.
     * If the character type is {@link CharacterType#ENEMY}, the dexterity tie-breaker
     * modifier defaults to zero.
     *
     * @param name       The unique display name of the character.
     * @param initiative The base integer initiative roll.
     * @param dex        The dexterity attribute score (typically 0–30) used for tie-breakers.
     * @param type       The {@link CharacterType} classification of this character.
     * @param status     The initial {@link DeathStatus} of this character.
     */
    public Character(String name, int initiative, int dex, CharacterType type, DeathStatus status) {
        characterName = name;
        characterType = type;
        deathStatus = status;
        deathSaves = new HashMap<>();
        dexValue = (characterType == CharacterType.ENEMY) ? 0 : (float) dex / 100;
        baseInitiative = initiative;
        initiativeValue = baseInitiative + dexValue;

        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
    }


    /**
     * Constructs a new Character instance using string representations for type and status.
     *
     * @param name       The unique display name of the character.
     * @param initiative The base integer initiative roll.
     * @param dex        The dexterity attribute score used for tie-breaking.
     * @param typeID     The string name representing the {@link CharacterType}.
     * @param deathID    The string name representing the {@link DeathStatus}.
     */
    public Character(String name, int initiative, int dex, String typeID, String deathID) {
        this(name, initiative, dex, typeFromString(typeID), deathFromString(deathID));
    }

    /**
     * Maps a case-sensitive string identifier to its corresponding {@link CharacterType}.
     *
     * @param id The name string of the character type.
     * @return The matching {@link CharacterType}, or {@link CharacterType#UNKNOWN} if unrecognized.
     */
    static CharacterType typeFromString(String id) {
        return switch (id) {
            case "PLAYER" -> CharacterType.PLAYER;
            case "ENEMY" -> CharacterType.ENEMY;
            case "ALLY" -> CharacterType.ALLY;
            case "HORDE" -> CharacterType.HORDE;
            default -> CharacterType.UNKNOWN;
        };
    }

    /**
     * Maps a case-sensitive string identifier to its corresponding {@link DeathStatus}.
     *
     * @param id The name string of the death status.
     * @return The matching {@link DeathStatus}, or {@link DeathStatus#UNKNOWN} if unrecognized.
     */
    static DeathStatus deathFromString(String id) {
        return switch (id) {
            case "DEAD" -> DeathStatus.DEAD;
            case "ALIVE" -> DeathStatus.ALIVE;
            case "DOWN" -> DeathStatus.DOWN;
            default -> DeathStatus.UNKNOWN;
        };
    }

    /**
     * Compiles a snapshot of the character's primary combat statistics into a key-value structure.
     * <p>
     * Keys provided in the map:
     * <ul>
     *   <li>{@code "Name"} - {@link String}</li>
     *   <li>{@code "Initiative"} - {@link Integer}</li>
     *   <li>{@code "Dexterity"} - {@link Integer}</li>
     *   <li>{@code "Type"} - {@link String}</li>
     *   <li>{@code "DeathStatus"} - {@link String}</li>
     * </ul>
     *
     * @return A {@link Map} populated with the character's core attributes.
     */
    public Map<String, Object> getCharacterData() {
        Map<String, Object> displayData = new HashMap<>();

        displayData.put("Name", characterName);
        displayData.put("Initiative", (int) baseInitiative);
        displayData.put("Dexterity",(int)(dexValue*100));
        displayData.put("TotalInitiative", initiativeValue);
        displayData.put("Type", characterType.name());
        displayData.put("DeathStatus", deathStatus.name());

        return displayData;
    }

    /**
     * Recalculates {@code initiativeValue} by combining the base initiative with the dexterity modifier.
     */
    private void recalculateInitiative() {
        initiativeValue = baseInitiative + dexValue;
    }

    /**
     * Updates the base initiative roll and recalculates the compound initiative score.
     *
     * @param newInitiativeNum The new base initiative score.
     */
    public void editInitiativeNum(float newInitiativeNum) {
        baseInitiative = newInitiativeNum;
        recalculateInitiative();
    }

    /**
     * Updates the dexterity attribute score and recalculates the compound initiative score.
     * <p>
     * If the character type is {@link CharacterType#ENEMY}, the tie-breaker is set to 0.
     *
     * @param newDexValue The new integer dexterity score (typically 0–30).
     */
    public void editDexValue(int newDexValue) {
        dexValue = (characterType == CharacterType.ENEMY) ? 0 : (float) newDexValue / 100;
        recalculateInitiative();
    }

    public void setDeathSaves(int passes, int fails) {
        deathSaves.put("Pass", passes);
        deathSaves.put("Fail", fails);
    }

    /**
     * Registers a single death saving throw result for a downed combatant.
     * <p>
     * Ignored if {@code deathStatus} is not {@link DeathStatus#DOWN}.
     * When successful passes reach 3, the character becomes {@link DeathStatus#ALIVE}
     * and death saves are reset. When failed saves reach 3, the character becomes
     * {@link DeathStatus#DEAD} and death saves are reset.
     *
     * @param pass {@code true} for a successful saving throw; {@code false} for a failure.
     */
    public void deathSave(boolean pass){
        if (deathStatus != DeathStatus.DOWN){
            return;
        }
        if (pass){
            deathSaves.put("Pass", deathSaves.get("Pass")+1);
            if (deathSaves.get("Pass") >=3){
                deathStatus = DeathStatus.ALIVE;
                deathSaves.put("Pass", 0);
                deathSaves.put("Fail", 0);
            }
        }else{
            deathSaves.put("Fail", deathSaves.get("Fail")+1);
            if (deathSaves.get("Fail") >=3){
                deathStatus = DeathStatus.DEAD;
                deathSaves.put("Pass", 0);
                deathSaves.put("Fail", 0);
            }
        }

    }

    /**
     * Clears all accumulated death save passes and failures back to zero.
     */
    public void resetDeathSaves() {
        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
    }

    /**
     * Updates the character's life status.
     * <p>
     * If the new status is anything other than {@link DeathStatus#DOWN}, all accumulated
     * death saves are automatically reset via {@link #resetDeathSaves()}.
     *
     * @param newDeathStatus The new {@link DeathStatus} to assign.
     */
    public void editDeathStatus(DeathStatus newDeathStatus) {
        deathStatus = newDeathStatus;
        if (newDeathStatus != DeathStatus.DOWN) {
            resetDeathSaves();
        }
    }

    /**
     * Updates the character's life status using a string identifier.
     *
     * @param statusID The string identifier matching a {@link DeathStatus} name.
     */
    public void editDeathStatus(String statusID) {
        editDeathStatus(deathFromString(statusID));
    }

    /**
     * Retrieves the fully calculated initiative value, including decimal tie-breakers.
     *
     * @return The composite floating-point initiative score.
     */
    public float getInitiativeValue() {
        return initiativeValue;
    }

    /**
     * Generates a descriptive string containing name, initiative, type, and status.
     *
     * @return A formatted text representation of this character.
     */
    @Override
    public String toString(){
        return "name: " + characterName +
                ". Initiative: " + initiativeValue +
                ". Type: " + characterType +
                ". DeathStatus: " + deathStatus;
    }


    public CharacterType getCharacterType() {
        return characterType;
    }

    /**
     * Retrieves the character's current life status.
     *
     * @return The current {@link DeathStatus}.
     */
    public DeathStatus getDeathStatus() {
        return deathStatus;
    }

    /**
     * Retrieves the map containing current pass and fail tallies for death saves.
     *
     * @return A map containing key-value pairs for {@code "Pass"} and {@code "Fail"}.
     */
    public Map<String, Integer> getDeathSaves() {
        return deathSaves;
    }
}