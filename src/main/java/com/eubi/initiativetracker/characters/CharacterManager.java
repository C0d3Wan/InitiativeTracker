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

    public void editCharacter(int characterIndex, int change, int Change) {
        //allows changing of character
        //0 for inititive 1 for death status
        if (change == 0){
            this.characters.elementAt(characterIndex).editInitiativeNum(change);
        }else if (change == 1){
            this.characters.elementAt(characterIndex).editDeathStatus((byte) change);
        }

    }

    public void clearCharacters() {
        this.characters.clear();
    }

    public Vector<Character> getCharacters() {
        return this.characters;
    }
}
