/*
 * Initiative Tracker
 * Copyright (C) 2026 Jacen Turk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.c0d3wan.initiativetracker;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.JsonToDatabaseImporter;
import com.c0d3wan.initiativetracker.filemanagement.saveload.ConfigManager;
import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.ui.MainWindow;
import com.c0d3wan.initiativetracker.ui.UIController;

import javax.swing.*;

/**
 * Entry point for the Initiative Tracker application.
 * <p>
 * Initialises core state management, loads persistent configuration settings,
 * and launches the Swing graphical user interface on the Event Dispatch Thread (EDT).
 */
public class Main {

    /**
     * Application bootstrapper.
     *
     * @param args Command-line arguments passed during startup (unused).
     */
    public static void main(String[] args) {
        try {
            JsonToDatabaseImporter.main(new String[0]);
        } catch (Exception e) {
            System.err.println("Warning: Failed to initialize bestiary database from resources: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            CharacterManager characterManager = new CharacterManager();
            MainWindow mainWindow = new MainWindow();
            UIController uiController = new UIController(mainWindow, characterManager);
            ConfigManager.loadConfig(uiController);
            uiController.displayMainWindow();
        });
    }
}