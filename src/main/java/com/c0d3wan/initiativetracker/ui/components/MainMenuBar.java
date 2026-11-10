package com.c0d3wan.initiativetracker.ui.components;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MainMenuBar extends JMenuBar {

    private final JMenuItem openItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem saveItem;
    private final JMenuItem exitItem;

    private final JMenuItem lightMode;
    private final JMenuItem darkMode;

    public MainMenuBar() {
        openItem = new JMenuItem("Open");
        saveAsItem = new JMenuItem("Save As");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        lightMode = new JMenuItem("Light");
        darkMode = new JMenuItem("Dark");

        setUpMenuBar();
    }

    private void setUpMenuBar() {
        if (FlatLaf.isLafDark()) {
            darkMode.setEnabled(false);
        } else {
            lightMode.setEnabled(false);
        }

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

    public void enableDarkButton(boolean enableDark) {
        if (enableDark) {
            lightMode.setEnabled(false);
            darkMode.setEnabled(true);
        } else {
            lightMode.setEnabled(true);
            darkMode.setEnabled(false);
        }
    }

    public void addLightModeListener(ActionListener listener) {
        lightMode.addActionListener(listener);
    }

    public void addDarkModeListener(ActionListener listener) {
        darkMode.addActionListener(listener);
    }

    public void addOpenItemListener(ActionListener listener) {
        openItem.addActionListener(listener);
    }

    public void addSaveItemListener(ActionListener listener) {
        saveItem.addActionListener(listener);
    }

    public void addSaveAsItemListener(ActionListener listener) {
        saveAsItem.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
        exitItem.addActionListener(listener);
    }
}
