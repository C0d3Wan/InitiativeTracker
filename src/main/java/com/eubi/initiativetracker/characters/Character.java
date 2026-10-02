package com.eubi.initiativetracker.characters;

import java.util.*;

/**
 * Represents a character entity within an initiative tracking system.
 * <p>
 * This class stores essential combat data for a character, including their name,
 * combat status, alignment type, and their calculated initiative. It uniquely
 * handles dexterity by converting it into a decimal value to act as a tie-breaker
 * for initiative rolls.
 */
public class Character {
    private final String characterName;
    private float initiativeValue;
    private float dexValue;
    private CharacterType characterType;
    private DeathStatus deathStatus;

    /**
     * Constructs a new Character with the specified starting attributes.
     * <p>
     * Note: The final initiative value is calculated by taking the base initiative
     * and adding the dexterity score divided by 100 as a decimal (e.g., an initiative
     * of 15 and a dexterity of 14 results in an initiativeValue of 15.14).
     *
     * @param name       The display name of the character.
     * @param initiative The base initiative roll or score.
     * @param dex        The dexterity score, used to calculate the fractional tie-breaker.
     * @param type       The initial {@link CharacterType} (alignment) of the character.
     * @param status     The initial {@link DeathStatus} of the character.
     */
    public Character(String name, int initiative, int dex, CharacterType type, DeathStatus status) {
        this.characterName = name;
        this.characterType = type;
        this.deathStatus = status;
        this.dexValue = (float) dex / 100;
        this.initiativeValue = initiative + this.dexValue;
    }

    /**
     * A different way of constructing a new Character with the specified starting attributes.
     *
     * @param name       The display name of the character.
     * @param initiative The base initiative roll or score.
     * @param dex        The dexterity score, used to calculate the fractional tie-breaker.
     * @param typeID       The initial id of the {@link CharacterType} (alignment) of the character.
     * @param deathID     The initial id of the  {@link DeathStatus} of the character.
     */
    public Character(String name, int initiative, int dex, int typeID, int deathID) {
        this.characterName = name;
        this.dexValue = (float) dex / 100;
        this.initiativeValue = initiative + this.dexValue;
        switch (typeID) {
            case 0:
                this.characterType = CharacterType.PLAYER;
                break;
            case 1:
                this.characterType = CharacterType.ENEMY;
                break;
            case 2:
                this.characterType = CharacterType.ALLY;
                break;
            default:
                this.characterType = CharacterType.UNKNOWN;
        }

        switch (deathID) {
            case 0:
                this.deathStatus = DeathStatus.DEAD;
                break;
            case 1:
                this.deathStatus = DeathStatus.ALIVE;
                break;
            case 2:
                this.deathStatus = DeathStatus.DOWN;
                break;
            default:
                this.deathStatus = DeathStatus.UNKNOWN;


        }
    }

    /**
     * A different way of constructing a new Character with the specified starting attributes.
     *
     * @param name       The display name of the character.
     * @param initiative The base initiative roll or score.
     * @param dex        The dexterity score, used to calculate the fractional tie-breaker.
     * @param typeID       The initial String of the {@link CharacterType} (alignment) of the character.
     * @param deathID     The initial String of the  {@link DeathStatus} of the character.
     */
    public Character(String name, int initiative, int dex, String typeID, String deathID) {
        this.characterName = name;
        this.dexValue = (float) dex / 100;
        this.initiativeValue = initiative + this.dexValue;
        switch (typeID) {
            case "Player":
                this.characterType = CharacterType.PLAYER;
                break;
            case "Enemy":
                this.characterType = CharacterType.ENEMY;
                break;
            case "Ally":
                this.characterType = CharacterType.ALLY;
                break;
            default:
                this.characterType = CharacterType.UNKNOWN;
        }

        switch (deathID) {
            case "Dead":
                this.deathStatus = DeathStatus.DEAD;
                break;
            case "Alive":
                this.deathStatus = DeathStatus.ALIVE;
                break;
            case "Down":
                this.deathStatus = DeathStatus.DOWN;
                break;
            default:
                this.deathStatus = DeathStatus.UNKNOWN;

        }
    }

    /**
     * Compiles and retrieves the core state of the character as a key-value data structure.
     *
     * @return A {@link Map} containing the character's Name, Initiative, Type,
     *         and DeathStatus mapped to their respective current values.
     */
    public Map<String, Object> getCharacterData() {
        Map<String, Object> displayData = new HashMap<>();

        displayData.put("Name", this.characterName);
        displayData.put("Initiative", this.initiativeValue);
        // The .name() method safely converts the Enum into a readable String
        displayData.put("Type", this.characterType.name());
        displayData.put("DeathStatus", this.deathStatus.name());

        return displayData;
    }

    /**
     * Updates the character's base initiative score.
     * <p>
     * The character's previously calculated decimal dexterity value is automatically
     * reapplied to the new base number to maintain tie-breaker formatting.
     *
     * @param newInitiativeNum The new base initiative score.
     */
    public void editInitiativeNum(float newInitiativeNum) {
        this.initiativeValue = newInitiativeNum + this.dexValue;
    }

    /**
     * Updates the character's dexterity stat
     *
     * @param newDexValue - The new Dex Value
     */
    public void editDexValue(int newDexValue) {this.dexValue = newDexValue;}

    /**
     * Updates the character's life status.
     *
     * @param newDeathStatus The new {@link DeathStatus} to apply.
     */
    public void editDeathStatus(DeathStatus newDeathStatus) {
        this.deathStatus = newDeathStatus;
    }

    /**
     * Allows updating the death status using Int
     *
     * @param statusID The new {@link DeathStatus} to apply.
     */
    public void editDeathStatus(int statusID) {
            switch (statusID) {
                case 0:
                    this.deathStatus = DeathStatus.DEAD;
                    break;
                case 1:
                    this.deathStatus = DeathStatus.ALIVE;
                    break;
                case 2:
                    this.deathStatus = DeathStatus.DOWN;
                    break;
                default:
                    this.deathStatus = DeathStatus.UNKNOWN;
            }
    }

    /**
     * Updates the character's category.
     *
     * @param newCharacterType The new {@link CharacterType} to apply.
     */
    public void editCharacterType(CharacterType newCharacterType) {
        this.characterType = newCharacterType;
    }

    /**
     * Allows updating the character's category using Int
     *
     * @param statusID The new {@link CharacterType} to apply.
     */
    public void editCharacterType(int statusID) {
        switch (statusID) {
            case 0:
                this.characterType = CharacterType.PLAYER;
                break;
            case 1:
                this.characterType = CharacterType.ENEMY;
                break;
            case 2:
                this.characterType = CharacterType.ALLY;
                break;
            default:
                this.characterType = CharacterType.UNKNOWN;
        }
    }

    /**
     * Retrieves the character's current, fully calculated initiative value.
     *
     * @return The initiative value, including the fractional dexterity modifier.
     */
    public float getInitiativeValue() {
        return this.initiativeValue;
    }

    /**
     * Returns a human-readable string representation of the character's current state.
     *
     * @return A formatted string detailing the character's name, initiative, type, and status.
     */
    @Override
    public String toString(){
        return "name: " + this.characterName +
                ". Initiative: " + this.initiativeValue +
                ". Type: " + this.characterType +
                ". DeathStatus: " + this.deathStatus;
    }
}