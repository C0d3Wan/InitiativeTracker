package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;

import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    static void main() {
        CharacterManager characterManager = new CharacterManager();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
    }
}
