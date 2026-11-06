package com.c0d3wan.initiativetracker.ui.components;

import com.c0d3wan.initiativetracker.ui.UIController;

import javax.swing.*;

public class MainMenuBar extends JMenuBar {
    private final UIController uiController;
    private final JFrame parentFrame;

    private final JMenuItem openItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem saveItem;
    private final JMenuItem exitItem;

    private final JMenuItem lightMode;
    private final JMenuItem darkMode;

    public MainMenuBar(JFrame parent, UIController manager) {
        uiController = manager;
        parentFrame = parent;

        openItem = new JMenuItem("Open");
        saveAsItem = new JMenuItem("Save As");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        lightMode = new JMenuItem("Light");
        darkMode = new JMenuItem("Dark");

        setUpMenuBar();
    }

    private void setUpMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu themeMenu = new JMenu("Themes");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(exitItem);
        add(fileMenu);

        themeMenu.add(darkMode);
        themeMenu.add(lightMode);
        viewMenu.add(themeMenu);
        add(viewMenu);
    }

    private void addActionListeners() {
        lightMode.addActionListener(e -> {
            UIController.setDarkMode(false);
            lightMode.setEnabled(false);
            darkMode.setEnabled(true);
        });
        darkMode.addActionListener(e -> {
            UIController.setDarkMode(true);
            lightMode.setEnabled(true);
            darkMode.setEnabled(false);
        });
        openItem.addActionListener(e -> uiController.loadList());
        saveItem.addActionListener(e -> uiController.saveList());
        saveAsItem.addActionListener(e -> uiController.saveListAs());
        exitItem.addActionListener(e -> confirmExit());
    }
}
