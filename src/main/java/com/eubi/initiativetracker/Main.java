package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;

import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    static void main() {
        CharacterManager characterManager = init();
        characterManager.addCharacter(new Character("1", 14, 12, "Player", "Alive"));
        characterManager.addCharacter(new Character("2", 8, 15, "Player", "Alive"));
        characterManager.addCharacter(new Character("3", 19, 18, "Player", "Alive"));
        characterManager.addCharacter(new Character("4", 3, 9, "Player", "Alive"));
        characterManager.addCharacter(new Character("5", 12, 16, "Player", "Alive"));
        characterManager.addCharacter(new Character("6", 17, 10, "Player", "Alive"));
        characterManager.addCharacter(new Character("7", 5, 8, "Player", "Alive"));
        characterManager.addCharacter(new Character("8", 11, 14, "Player", "Alive"));
        characterManager.addCharacter(new Character("9", 2, 7, "Player", "Alive"));
        characterManager.addCharacter(new Character("10", 20, 19, "Player", "Alive"));
        characterManager.addCharacter(new Character("11", 15, 13, "Player", "Alive"));
        characterManager.addCharacter(new Character("12", 9, 11, "Player", "Alive"));
        characterManager.addCharacter(new Character("13", 4, 15, "Player", "Alive"));
        characterManager.addCharacter(new Character("14", 18, 17, "Player", "Alive"));
        characterManager.addCharacter(new Character("15", 7, 10, "Player", "Alive"));
        characterManager.addCharacter(new Character("16", 13, 12, "Player", "Alive"));
        characterManager.addCharacter(new Character("17", 1, 9, "Player", "Alive"));
        characterManager.addCharacter(new Character("18", 16, 14, "Player", "Alive"));
        characterManager.addCharacter(new Character("19", 10, 8, "Player", "Alive"));
        characterManager.addCharacter(new Character("20", 6, 16, "Player", "Alive"));
        characterManager.addCharacter(new Character("21", 14, 11, "Player", "Alive"));
        characterManager.addCharacter(new Character("22", 19, 20, "Player", "Alive"));
        characterManager.addCharacter(new Character("23", 8, 13, "Player", "Alive"));
        characterManager.addCharacter(new Character("24", 12, 18, "Player", "Alive"));
        characterManager.addCharacter(new Character("25", 3, 7, "Player", "Alive"));
        characterManager.addCharacter(new Character("26", 17, 15, "Player", "Alive"));
        characterManager.addCharacter(new Character("27", 11, 10, "Player", "Alive"));
        characterManager.addCharacter(new Character("28", 5, 14, "Player", "Alive"));
        characterManager.addCharacter(new Character("29", 20, 12, "Player", "Alive"));
        characterManager.addCharacter(new Character("30", 9, 16, "Player", "Alive"));
        characterManager.addCharacter(new Character("31", 2, 8, "Player", "Alive"));
        characterManager.addCharacter(new Character("32", 15, 19, "Player", "Alive"));
        characterManager.addCharacter(new Character("33", 4, 11, "Player", "Alive"));
        characterManager.addCharacter(new Character("34", 18, 13, "Player", "Alive"));
        characterManager.addCharacter(new Character("35", 13, 17, "Player", "Alive"));
        characterManager.addCharacter(new Character("36", 7, 9, "Player", "Alive"));
        characterManager.addCharacter(new Character("37", 16, 15, "Player", "Alive"));
        characterManager.addCharacter(new Character("38", 10, 14, "Player", "Alive"));
        characterManager.addCharacter(new Character("39", 1, 12, "Player", "Alive"));
        characterManager.addCharacter(new Character("40", 6, 18, "Player", "Alive"));
        characterManager.debugDisplay();

    }

    private static CharacterManager init(){
        //Initializes UI and Character Manager
        CharacterManager characterManager = new CharacterManager();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
        return characterManager;
    }
}

