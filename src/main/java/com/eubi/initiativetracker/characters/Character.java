package com.eubi.initiativetracker.characters;
import java.util.*;

public class Character {
    private final String characterName;
    private float initiativeValue;
    private float dexValue;
    private byte characterType;
    private byte deathStatus;

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
}
