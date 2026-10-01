package com.eubi.initiativetracker.characters;
import java.util.*;

/**
 * Represents a character entity within an initiative tracking system.
 * <p>
 *     This class store essential data for a characer, including thier name,
 *     combat status, type and calculated initiative. It uses dexterity by
 *     converting it into a tie-break for initiative.</p>
 */
public class Character {
    private final String characterName;
    private float initiativeValue;
    private float dexValue;
    private String characterType;
    private String deathStatus;

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
     * @param type       The initial category of the character (e.g., "Player", "Enemy").
     * @param status     The initial life status of the character (e.g., "Alive", "Dead").
     */
    public Character(String name, int initiative, int dex, String type, String status) {
        this.characterName = name;
        this.characterType = type;
        this.deathStatus = status;
        this.dexValue = (float) dex /100;

        this.initiativeValue = initiative + this.dexValue;
    }

    /**
     * Compiles and retrieves the core state of the character as a key-value data structure.
     *
     * @return A {@link Dictionary} containing the character's Name, Initiative, Type,
     *         and DeathStatus mapped to their respective current values.
     */
    public Dictionary getCharacterData() {
        //Returns the stored data as a dictionary
        Dictionary<String, Object> displayData = new Hashtable<>();

        displayData.put("Name", this.characterName);
        displayData.put("Initiative", this.initiativeValue);
        displayData.put("Type", this.characterType);
        displayData.put("DeathStatus", this.deathStatus);

        return displayData;
    }

    /**
     * Updates the character's initiative score.
     * <p>
     * The character's previously calculated decimal dexterity value is automatically
     * reapplied to the new  number to maintain tie-breaker formatting.
     *
     * @param newInitiativeNum The new initiative score.
     */
    public void editInitiativeNum(float newInitiativeNum) {
        //Edits the initiative of the Character
        this.initiativeValue = newInitiativeNum + this.dexValue;
    }

    /**
     * Updates the character's life status based on a specific integer mapping.
     *
     * @param newDeathStatus An integer representing the new status:
     *                       0 = "Dead", 1 = "Alive", 2 = "Down".
     *                       Any other value defaults to "Unknown".
     */
    public void editDeathStatus(int newDeathStatus) {
        //edits the death status of the character
        if (newDeathStatus == 0) {
            this.deathStatus = "Dead";
        }else if (newDeathStatus == 1) {
            this.deathStatus = "Alive";
        }else if (newDeathStatus == 2) {
            this.deathStatus = "Down";
        }else{
            this.deathStatus = "Unknown";
        }

    }

    /**
     * Updates the character's category based on a specific integer mapping.
     *
     * @param newCharacterType An integer representing the new type:
     *                         0 = "Player", 1 = "Enemy", 2 = "Ally".
     *                         Any other value defaults to "Unknown".
     */
    public void editCharacterType(int newCharacterType) {
        //edits the Character Type of the character
        if (newCharacterType == 0) {
            this.characterType = "Player";
        }else if (newCharacterType== 1) {
            this.characterType = "Enemy";
        }else if (newCharacterType == 2) {
            this.characterType = "Ally";
        }else{
            this.characterType = "Unknown";
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
    public String toString(){
        return "name: "+this.characterName + ". Initiative: " +this.initiativeValue + ". Type: " +  this.characterType + ". DeathStatus: " + this.deathStatus;
    }
}
