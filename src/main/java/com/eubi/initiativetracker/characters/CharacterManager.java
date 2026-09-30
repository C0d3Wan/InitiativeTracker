package com.eubi.initiativetracker.characters;
import java.util.*;

public class CharacterManager {
    private Vector<Character> characters;

    public CharacterManager() {
        this.characters = new Vector<Character>();
    }

    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    public void removeCharacter(Character character) {
        this.characters.remove(character);
    }

    public void editCharacter(int characterIndex, int change, int newVal) {
        //allows changing of character
        //0 for inititive 1 for death status
        if (change == 0){
            this.characters.elementAt(characterIndex).editInitiativeNum(newVal);
        }else if (change == 1){
            this.characters.elementAt(characterIndex).editDeathStatus(newVal);
        }else{
            System.err.println("Invalid change");
        }

    }

    public void clearCharacters() {
        this.characters.clear();
    }

    public Character getCharacter(int characterIndex) {
        try {
            return this.characters.elementAt(characterIndex);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    public Vector<Character> getCharacterVector() {return this.characters;}
}
