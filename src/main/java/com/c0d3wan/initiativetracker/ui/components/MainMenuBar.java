package com.c0d3wan.initiativetracker.ui.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MainMenuBar extends JMenuBar {

    private final JMenuItem openItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem saveItem;
    private final JMenuItem exitItem;

    private final JMenuItem basicLightMode;
    private final JMenuItem basicDarkMode;
    private final JMenuItem arcLightMode;
    private final JMenuItem arcLightModeOrange;
    private final JMenuItem arcDarkMode;
    private final JMenuItem arcDarkModeOrange;

    private final JMenuItem[] lightButtons;
    private final JMenuItem[] darkButtons;

    public MainMenuBar() {
        openItem = new JMenuItem("Open");
        saveAsItem = new JMenuItem("Save As");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        basicLightMode = new JMenuItem("Default Light");
        basicDarkMode = new JMenuItem("Default Dark");
        arcLightMode = new JMenuItem("Arc Light (Default)");
        arcLightModeOrange = new JMenuItem("Arc Light (Orange)");
        arcDarkMode = new JMenuItem("Arc Dark (Default)");
        arcDarkModeOrange = new JMenuItem("Arc Dark (Orange)");

        lightButtons = new JMenuItem[]{basicLightMode, arcLightMode, arcLightModeOrange};
        darkButtons = new JMenuItem[]{basicDarkMode, arcDarkMode, arcDarkModeOrange};

        setUpMenuBar();
    }

    private void setUpMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");

        JMenu lightMenu = new JMenu("Light Themes");
        JMenu darkMenu = new JMenu("Dark Themes");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(exitItem);
        add(fileMenu);

        for (JMenuItem item : lightButtons) {
            lightMenu.add(item);
        }
        for (JMenuItem item : darkButtons) {
            darkMenu.add(item);
        }

        viewMenu.add(lightMenu);
        viewMenu.add(darkMenu);
        add(viewMenu);
    }

    public void disableThemeButton(int theme) {
        for (JMenuItem item : lightButtons) {
            item.setEnabled(true);
        }
        for (JMenuItem item : darkButtons) {
            item.setEnabled(true);
        }

        switch (theme) {
            case 0 -> basicLightMode.setEnabled(false);
            case 1 -> basicDarkMode.setEnabled(false);
            case 2 -> arcLightMode.setEnabled(false);
            case 3 -> arcLightModeOrange.setEnabled(false);
            case 4 -> arcDarkMode.setEnabled(false);
            case 5 -> arcDarkModeOrange.setEnabled(false);
        }
    }

    public void addBasicLightListener(ActionListener listener) {
        basicLightMode.addActionListener(listener);
    }

    public void addBasicDarkListener(ActionListener listener) {
        basicDarkMode.addActionListener(listener);
    }

    public void addArcLightListener(ActionListener listener) {
        arcLightMode.addActionListener(listener);
    }

    public void addArcLightOrangeListener(ActionListener listener) {
        arcLightModeOrange.addActionListener(listener);
    }

    public void addArcDarkListener(ActionListener listener) {
        arcDarkMode.addActionListener(listener);
    }

    public void addArcDarkOrangeListener(ActionListener listener) {
        arcDarkModeOrange.addActionListener(listener);
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
