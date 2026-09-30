package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;

import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

public class Main {
    static void main() {
        Character character = new Character("Jasoon", 20,0, (byte)1);
      
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
        System.out.println(character.getCharacterData());
    }
}
