package com.eubi.initiativetracker.characters;

public class Character {
    private int initiativeNum;
    private final String characterName;

    public Character(int initiativeNum, String characterName) {
        this.initiativeNum = initiativeNum;
        this.characterName = characterName;
    }
    public int getInitiativeNum() {
        return initiativeNum;
    }
    public String getCharacterName() {
        return characterName;
    }
}
