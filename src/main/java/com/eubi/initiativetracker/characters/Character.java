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
    private float baseInitiative;
    private float initiativeValue;
    private float dexValue;
    private boolean currentCharacter;
    private Map<String, Integer> deathSaves;
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
        characterName = name;
        characterType = type;
        deathStatus = status;
        currentCharacter = false;
        deathSaves = new HashMap<>();
        dexValue = (float) dex / 100;
        baseInitiative = initiative;
        initiativeValue = baseInitiative + dexValue;

        deathSaves.put("Pass", 0);
        deathSaves.put("Fail", 0);
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
        this(name, initiative, dex, typeFromId(typeID), deathFromId(deathID));
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
        this(name, initiative, dex, typeFromString(typeID), deathFromString(deathID));
    }

    private static CharacterType typeFromId(int id) {
        return switch (id) {
            case 0 -> CharacterType.PLAYER;
            case 1 -> CharacterType.ENEMY;
            case 2 -> CharacterType.ALLY;
            default -> CharacterType.UNKNOWN;
        };
    }

    private static DeathStatus deathFromId(int id) {
        return switch (id) {
            case 0 -> DeathStatus.DEAD;
            case 1 -> DeathStatus.ALIVE;
            case 2 -> DeathStatus.DOWN;
            default -> DeathStatus.UNKNOWN;
        };
    }

    private static CharacterType typeFromString(String id) {
        return switch (id) {
            case "PLAYER" -> CharacterType.PLAYER;
            case "ENEMY" -> CharacterType.ENEMY;
            case "ALLY" -> CharacterType.ALLY;
            default -> CharacterType.UNKNOWN;
        };
    }

    private static DeathStatus deathFromString(String id) {
        return switch (id) {
            case "DEAD" -> DeathStatus.DEAD;
            case "ALIVE" -> DeathStatus.ALIVE;
            case "DOWN" -> DeathStatus.DOWN;
            default -> DeathStatus.UNKNOWN;
        };
    }


    /**
     * Compiles and retrieves the core state of the character as a key-value data structure.
     *
     * @return A {@link Map} containing the character's Name, Initiative, Type,
     *         and DeathStatus mapped to their respective current values.
     */
    public Map<String, Object> getCharacterData() {
        Map<String, Object> displayData = new HashMap<>();

        displayData.put("Name", characterName);
        displayData.put("Initiative", (int) baseInitiative);
        displayData.put("Dexterity",(int)(dexValue*100));
        displayData.put("Type", characterType.name());
        displayData.put("DeathStatus", deathStatus.name());

        return displayData;
    }


    private void recalculateInitiative() {
        initiativeValue = baseInitiative + dexValue;
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
        baseInitiative = newInitiativeNum;
        recalculateInitiative();
    }

    /**
     * Updates the character's dexterity stat
     *
     * @param newDexValue - The new Dex Value
     */
    public void editDexValue(int newDexValue) {
        dexValue = (characterType == CharacterType.ENEMY) ? 0 : (float) newDexValue / 100;
        recalculateInitiative();
    }

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
     * Updates the character's life status.
     *
     * @param newDeathStatus The new {@link DeathStatus} to apply.
     */
    public void editDeathStatus(DeathStatus newDeathStatus) {
        deathStatus = newDeathStatus;
    }

    /**
     * Allows updating the death status using Int
     *
     * @param statusID The new {@link DeathStatus} to apply.
     */
    public void editDeathStatus(int statusID) {
            switch (statusID) {
                case 0:
                    deathStatus = DeathStatus.DEAD;
                    break;
                case 1:
                    deathStatus = DeathStatus.ALIVE;
                    break;
                case 2:
                    deathStatus = DeathStatus.DOWN;
                    break;
                default:
                    deathStatus = DeathStatus.UNKNOWN;
            }
    }

    /**
     * Allows updating the death status using String
     *
     * @param statusID The new {@link DeathStatus} to apply.
     */
    public void editDeathStatus(String statusID) {
        switch (statusID) {
            case "DEAD":
                deathStatus = DeathStatus.DEAD;
                break;
            case "ALIVE":
                deathStatus = DeathStatus.ALIVE;
                break;
            case "DOWN":
                deathStatus = DeathStatus.DOWN;
                break;
            default:
                deathStatus = DeathStatus.UNKNOWN;
        }
    }

    /**
     * Updates the character's category.
     *
     * @param newCharacterType The new {@link CharacterType} to apply.
     */
    public void editCharacterType(CharacterType newCharacterType) {
        characterType = newCharacterType;
    }

    /**
     * Allows updating the character's category using Int
     *
     * @param statusID The new {@link CharacterType} to apply.
     */
    public void editCharacterType(int statusID) {
        switch (statusID) {
            case 0:
                characterType = CharacterType.PLAYER;
                break;
            case 1:
                characterType = CharacterType.ENEMY;
                break;
            case 2:
                characterType = CharacterType.ALLY;
                break;
            default:
                characterType = CharacterType.UNKNOWN;
        }
    }

    public void editCurrentCharacter(boolean current){
        currentCharacter = current;
    }

    public boolean isCurrentCharacter() { return currentCharacter; }

    /**
     * Retrieves the character's current, fully calculated initiative value.
     *
     * @return The initiative value, including the fractional dexterity modifier.
     */
    public float getInitiativeValue() {
        return initiativeValue;
    }

    /**
     * Returns a human-readable string representation of the character's current state.
     *
     * @return A formatted string detailing the character's name, initiative, type, and status.
     */
    @Override
    public String toString(){
        return "name: " + characterName +
                ". Initiative: " + initiativeValue +
                ". Type: " + characterType +
                ". DeathStatus: " + deathStatus;
    }

    public DeathStatus getDeathStatus() {
        return deathStatus;
    }

    public Map<String, Integer> getDeathSaves() {
        return deathSaves;
    }
}