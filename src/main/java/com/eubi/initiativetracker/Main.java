package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;

public class Main {
    static void main() {
        //tests
        CharacterManager characterManager = new CharacterManager();
        Character character = new Character("Jasoon", 20,0, "Player",  "Alive");
        characterManager.addCharacter(character);
        characterManager.addCharacter(new Character("Kristof", 17,5, "Enemy", "Alive"));
        characterManager.debugDisplay();
        characterManager.editCharacter(0,0,12);
        characterManager.debugDisplay();
        characterManager.editCharacter(0,1,2);
        characterManager.editCharacter(1,1,4);
        characterManager.debugDisplay();
        characterManager.editCharacter(0,1,1);
        characterManager.editCharacter(1,1,0);
        characterManager.editCharacter(0,2,2);
        characterManager.editCharacter(1,2,4);
        characterManager.debugDisplay();
        characterManager.removeCharacter(character);
        characterManager.debugDisplay();
    }
}
