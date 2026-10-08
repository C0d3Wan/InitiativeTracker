package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.swingui.MainMenu;

import javax.swing.*;

/**
 * Entry point for launching the D&D Initiative Tracker application.
 */
public class Main {
    /**
     * Starts the application by initializing core managers and launching the GUI.
     *
     * @param args Command-line arguments passed during startup (unused).
     */
    public static void main(String[] args) {
        CharacterManager characterManager = init();
    }

    /**
     * Instantiates necessary models and schedules the primary {@link MainMenu}
     * window for rendering on the Swing Event Dispatch Thread (EDT).
     *
     * @return The initialized {@link CharacterManager} instance.
     */
    private static CharacterManager init(){
        CharacterManager characterManager = new CharacterManager();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
        return characterManager;
    }
}