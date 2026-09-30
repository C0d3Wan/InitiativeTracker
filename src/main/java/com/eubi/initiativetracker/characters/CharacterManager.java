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

    public void clearCharacters() {
        this.characters.clear();
    }

    public Vector<Character> getCharacters() {
        return this.characters;
    }
}
