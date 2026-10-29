package com.c0d3wan.initiativetracker;
import com.c0d3wan.initiativetracker.FileManagement.ConfigManager;
import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.swingui.MainMenu;

import javax.swing.*;

/**
 * Entry point for the Initiative Tracker application.
 * <p>
 * Initializes core state management, loads persistent configuration settings,
 * and launches the Swing graphical user interface on the Event Dispatch Thread (EDT).
 */
public class Main {

    /**
     * Application bootstrapper.
     *
     * @param args Command-line arguments passed during startup (unused).
     */
    public static void main(String[] args) {
        CharacterManager characterManager = new CharacterManager();
        ConfigManager.loadConfig();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
    }
}