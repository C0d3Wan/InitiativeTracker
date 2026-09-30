package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;

public class Main {
    static void main() {
        //tests
        CharacterManager characterManager = new CharacterManager();
        Character character = new Character("Jasoon", 20,0, (byte)1);
        characterManager.addCharacter(character);
        characterManager.addCharacter(new Character("Kristof", 17,5, (byte)0));
        System.out.println(characterManager.getCharacter(0).getCharacterData());
        System.out.println(characterManager.getCharacter(1).getCharacterData());
        characterManager.editCharacter(0,1,0);
        characterManager.editCharacter(1,0,17);
        System.out.println(characterManager.getCharacter(0).getCharacterData());
        System.out.println(characterManager.getCharacter(1).getCharacterData());
        System.out.println(characterManager.getCharacterVector());
        characterManager.removeCharacter(characterManager.getCharacterVector().elementAt(1));
        System.out.println(characterManager.getCharacterVector());
    }
}
