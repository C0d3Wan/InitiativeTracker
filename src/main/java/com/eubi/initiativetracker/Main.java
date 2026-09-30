package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;
import java.util.*;

public class Main {
    static void main() {
        CharacterManager characterManager = new CharacterManager();
        Character character = new Character("Jasoon", 20,0, (byte)1);
        characterManager.addCharacter(character);
        characterManager.addCharacter(new Character("Kristof", 17,5, (byte)0));
        System.out.println(characterManager.getCharacters());
        System.out.println(characterManager.getCharacters().elementAt(0).getCharacterData());
        System.out.println(characterManager.getCharacters().elementAt(1).getCharacterData());
    }
}
