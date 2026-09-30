package com.eubi.initiativetracker.characters;
import java.util.*;

public class Character {
    private final String characterName; //Name of the Character/Player
    private float initiativeValue; //Initiative Value is the Player Initiative + the Players Dex/100
    private float dexValue; // The Characters Dex value (0-30)
    private String characterType; //The type of character it is 0:Player 1:Enemy 2: Ally
    private String deathStatus; //The Death Status 0:Dead 1: Alive 2: Down/Dying

    public Character(String Name, int initiative, int dex, String Type, String Status) {
        this.characterName = Name;
        this.characterType = Type;
        this.deathStatus = Status;
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
    }

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
}
