package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.CharacterType;
import com.eubi.initiativetracker.characters.DeathStatus;
import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    static void main() {
        CharacterManager characterManager = init();
        characterManager.addCharacter(new Character("Jacen", 10, 12, 0, 1));
        characterManager.addCharacter(new Character("Ewan", 13, 2, "Player", "Dead"));
        characterManager.addCharacter(new Character("Kristof", 13, 11, CharacterType.ENEMY, DeathStatus.DOWN));
        characterManager.addCharacter(new Character("???", 0, 0, CharacterType.UNKNOWN, DeathStatus.UNKNOWN));
        characterManager.debugDisplay();
    }

    /**
     * Instantiantes all required Classes and UI
     *
     * @return The {@link CharacterManager} to be used throughout the Project
     */
    private static CharacterManager init(){
        CharacterManager characterManager = new CharacterManager();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
        return characterManager;
    }
}