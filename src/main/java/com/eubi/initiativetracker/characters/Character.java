package com.eubi.initiativetracker.characters;
import java.util.*;

public class Character {
    private final String characterName; //Name of the Character/Player
    private float initiativeValue; //Initiative Value is the Player Initiative + the Players Dex/100
    private float dexValue; // The Characters Dex value (0-30)
    private byte characterType; //The type of charcter it is 0:Player 1:Enemy 2: Ally
    private byte deathStatus; //The Death Status 0:Dead 1: Alive 2: Down/Dying

    public Character(String Name, int initiative, int dex, byte Type) {
        this.characterName = Name;
        this.characterType = Type;
        this.deathStatus = 1;
        this.dexValue = (float) dex /100;

        this.initiativeValue = initiative + this.dexValue;
    }

    public Dictionary getCharacterData() {
        //Returns the stored data as a dictionary
        Dictionary<String, Object> displayData = new Hashtable<>();

        displayData.put("Name", this.characterName);
        displayData.put("Initiative", this.initiativeValue);
        displayData.put("Type", this.characterType);
        displayData.put("DeathStatus", this.deathStatus);

        return displayData;
    }

    public void editInitiativeNum(float newInitiativeNum) {
        //Edits the initiative of the Character
        this.initiativeValue = newInitiativeNum + this.dexValue;
        //testcomment
    }

    public void editDeathStatus(byte newDeathStatus) {
        //edits the death status of the character
        this.deathStatus = newDeathStatus;
    }
}
