package com.eubi.initiativetracker.characters;

public class Character {
    private int initiativeNum;
    private final String characterName;
    private String characterType;
    private String aliveStatus;

    public Character(int initiativeNum, String characterName, String characterType) {
        this.initiativeNum = initiativeNum;
        this.characterName = characterName;
        this.characterType = characterType;
        this.aliveStatus = "Alive";
    }
    public int getInitiativeNum() {
        return initiativeNum;
    }
    public String getCharacterName() {
        return characterName;
    }
}
