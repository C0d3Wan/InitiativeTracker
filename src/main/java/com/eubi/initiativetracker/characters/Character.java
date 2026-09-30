package com.eubi.initiativetracker.characters;
import java.util.*;

public class Character {
    private final String characterName;
    private float initiativeValue;
    private float dexValue;
    private String characterType;
    private String deathStatus;

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
}
