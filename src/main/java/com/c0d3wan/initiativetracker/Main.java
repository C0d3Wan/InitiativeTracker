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
import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.swingui.MainMenu;
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
