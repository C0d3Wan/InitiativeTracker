package com.c0dewan.initiativetracker;
import com.c0dewan.initiativetracker.characters.CharacterManager;
import com.c0dewan.initiativetracker.swingui.MainMenu;
import com.formdev.flatlaf.FlatDarkLaf;

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
        CharacterManager characterManager = new CharacterManager();

        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new MainMenu(characterManager).setVisible(true));
    }
}