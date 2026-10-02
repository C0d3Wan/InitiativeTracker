package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.CharacterType;
import com.eubi.initiativetracker.characters.DeathStatus;
import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        CharacterManager characterManager = init();
        characterManager.debugDisplay();
    }

    /**
     * Instantiates all required Classes and UI
     *
     * @return The {@link CharacterManager} to be used throughout the Project
     */
    private static CharacterManager init(){
        CharacterManager characterManager = new CharacterManager();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
        return characterManager;
    }
}