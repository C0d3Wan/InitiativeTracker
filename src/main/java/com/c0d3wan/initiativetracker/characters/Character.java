package com.c0d3wan.initiativetracker.characters;

import java.util.*;

/**
 * Represents a base combatant entity within the initiative tracking system.
 * <p>
 * Manages core statistics including base initiative, dexterity tie-breaker values,
 * dynamic initiative score calculations, death saving throw tallies, and life states.
 */
public class Character {
    private final String characterName;
    private int baseInitiative;
    private int dexterity;
    private final Map<String, Integer> deathSaves;
    private final CharacterType characterType;
    private DeathStatus deathStatus;

    /**
     * Constructs a new {@code Character} instance.
     * <p>
     * Note: If {@code type} is {@link CharacterType#ENEMY}, dexterity is automatically
     * zeroed out to prevent tie-breaker bonuses.
     *
     * @param name       The display name of the character.
     * @param initiative The rolled base initiative score.
     * @param dex        The dexterity value used as an initiative tie-breaker.
     * @param type       The {@link CharacterType} classification.
     * @param status     The initial {@link DeathStatus}.
     */
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

    /**
     * Constructs a new {@code Character} using string representations for type and death status.
     *
     * @param name       The display name of the character.
     * @param initiative The rolled base initiative score.
     * @param dex        The dexterity value used as an initiative tie-breaker.
     * @param typeID     Case-insensitive string representation of {@link CharacterType}.
     * @param deathID    Case-insensitive string representation of {@link DeathStatus}.
     */
    public Character(String name, int initiative, int dex, String typeID, String deathID) {
        this(name, initiative, dex, typeFromString(typeID), deathFromString(deathID));
    }

    /**
     * Parses a string into a {@link CharacterType}.
     *
     * @param id The name of the character type to resolve.
     * @return The matched {@link CharacterType}, or {@link CharacterType#UNKNOWN} if invalid or null.
     */
    public static CharacterType typeFromString(String id) {
        try{
            return CharacterType.valueOf(id.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException | NullPointerException e){
            return CharacterType.UNKNOWN;
        }
    }

    /**
     * Parses a string into a {@link DeathStatus}.
     *
     * @param id The name of the death status to resolve.
     * @return The matched {@link DeathStatus}, or {@link DeathStatus#UNKNOWN} if invalid or null.
     */
    public static DeathStatus deathFromString(String id) {
        try{
            return DeathStatus.valueOf(id.toUpperCase(Locale.ROOT));
        } catch(IllegalArgumentException | NullPointerException e){
            return DeathStatus.UNKNOWN;
        }
    }

    /**
     * Creates an immutable snapshot of the character's current state.
     *
     * @return A {@link CharacterData} record populated with current values.
     */
    public CharacterData getCharacterData() {
        return new CharacterData(
                characterName,
                baseInitiative,
                dexterity,
                getInitiativeValue(),
                characterType,
                deathStatus);
    }

    /**
     * Sets the base rolled initiative score.
     *
     * @param initiative The new base initiative value.
     */
    public void setInitiative(int initiative) {
        baseInitiative = initiative;
    }

    /**
     * Sets the dexterity tie-breaker value.
     * <p>
     * If this character is of type {@link CharacterType#ENEMY}, the dexterity value is forced to 0.
     *
     * @param dexterity The dexterity score to set.
     */
    public void setDexterity(int dexterity) {
        this.dexterity = (characterType == CharacterType.ENEMY) ? 0 : dexterity;
    }

    /**
     * Manually overrides the counts for successful and failed death saving throws.
     *
     * @param passes The number of successful death saves.
     * @param fails  The number of failed death saves.
     */
    public void setDeathSaves(int passes, int fails) {
        deathSaves.put("Pass", passes);
        deathSaves.put("Fail", fails);
    }

    /**
     * Records a death saving throw outcome for a downed combatant.
     * <p>
     * Ignored if the character is not currently {@link DeathStatus#DOWN}.
     * Reaching 3 passes revives the character to {@link DeathStatus#ALIVE}.
     * Reaching 3 fails marks the character as {@link DeathStatus#DEAD}.
     * In both resolution cases, the death saves tally resets.
     *
     * @param pass {@code true} to record a success; {@code false} to record a failure.
     */
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

    /**
     * Resets both passed and failed death saving throw counters back to zero.
     */
    public void resetDeathSaves() {
        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
    }

    /**
     * Updates the combatant's death status.
     * <p>
     * Automatically clears any accumulated death saving throws if the status
     * transitions to anything other than {@link DeathStatus#DOWN}.
     *
     * @param newDeathStatus The new {@link DeathStatus} to apply.
     */
    public void setDeathStatus(DeathStatus newDeathStatus) {
        deathStatus = newDeathStatus;
        if (newDeathStatus != DeathStatus.DOWN) {
            resetDeathSaves();
        }
    }

    /**
     * Updates the combatant's death status using a string identifier.
     *
     * @param statusID The string identifier for the target death status.
     */
    public void setDeathStatus(String statusID) {
        setDeathStatus(deathFromString(statusID));
    }

    /**
     * Calculates the total effective initiative score used for ordering.
     * <p>
     * If the character is an enemy, returns the base initiative. Otherwise, adds
     * {@code dexterity / 100f} as a fractional tie-breaker.
     *
     * @return The combined initiative floating-point score.
     */
    public float getInitiativeValue() {
        return baseInitiative + (characterType == CharacterType.ENEMY ? 0 : dexterity / 100f);
    }

    /**
     * Gets the character's display name.
     *
     * @return The character's name.
     */
    public String getName() {
        return characterName;
    }

    /**
     * Gets the character's classification type.
     *
     * @return The {@link CharacterType}.
     */
    public CharacterType getCharacterType() {
        return characterType;
    }

    /**
     * Gets the current death/alive status.
     *
     * @return The {@link DeathStatus}.
     */
    public DeathStatus getDeathStatus() {
        return deathStatus;
    }

    /**
     * Gets the base rolled initiative score before tie-breakers.
     *
     * @return The raw initiative integer.
     */
    public int getBaseInitiative() {
        return baseInitiative;
    }

    /**
     * Gets the stored dexterity value.
     *
     * @return The dexterity score.
     */
    public int getDexterity() {
        return dexterity;
    }

    /**
     * Returns an unmodifiable view of the death saving throws tally.
     *
     * @return An unmodifiable {@link Map} with keys "Pass" and "Fail".
     */
    public Map<String, Integer> getDeathSaves() {
        return Collections.unmodifiableMap(deathSaves);
    }
}