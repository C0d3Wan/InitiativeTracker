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

import com.c0d3wan.initiativetracker.filemanagement.bestiary.BestiarySyncManager;
import com.c0d3wan.initiativetracker.filemanagement.saveload.ConfigManager;
import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.swingui.MainWindow;

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
        CharacterManager characterManager = new CharacterManager();
        ConfigManager.loadConfig();

        BestiarySyncManager.syncOfficialMonsters();

        SwingUtilities.invokeLater(() -> new MainWindow(characterManager).setVisible(true));
    }
}