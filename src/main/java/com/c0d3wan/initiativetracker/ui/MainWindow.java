package com.c0d3wan.initiativetracker.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.c0d3wan.initiativetracker.filemanagement.saveload.ConfigManager;
import com.c0d3wan.initiativetracker.ui.components.*;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Top-level application window hosting the main initiative tracker user interface.
 */
public class MainWindow extends JFrame {
    private final JPanel mainMenuPane = new JPanel();

    private final MainMenuBar menuBar;
    private final CharacterOptionsPanel characterOptionsPanel;
    private final InitiativeListPanel initiativeListPanel;
    private final CharacterDetailsPanel characterDetailsPanel;
    private final JTabbedPane tabs = new JTabbedPane();

    /**
     * Constructs and initialises the main application window.
     */
    public MainWindow() {
        initiativeListPanel = new InitiativeListPanel();
        characterDetailsPanel = new CharacterDetailsPanel();
        menuBar = new MainMenuBar();
        characterOptionsPanel = new CharacterOptionsPanel();
        setupMainMenu();

        setJMenuBar(menuBar);
        setContentPane(mainMenuPane);
        setTitle("Initiative Tracker");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    /**
     * Sets up the layout, layout manager, widgets and border for the Window
     */
    private void setupMainMenu() {
        Dimension windowSize = new Dimension(650, 400);
        mainMenuPane.setLayout(new MigLayout("insets 0 0 2 0, fill", "[c][c][c][c]", "[center][center][center][center][center]"));
        mainMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        tabs.addTab("Initiative Order", initiativeListPanel);
        tabs.addTab("Details", characterDetailsPanel);
        tabs.addTab("Character Options", characterOptionsPanel);

        mainMenuPane.add(tabs, "span, grow, push");
    }

    /**
     * prompts the user to ensure they want to exit the application
     */
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Tracker?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            ConfigManager.saveConfig();
            System.exit(0);
        }
    }

    public InitiativeListPanel getInitiativeListPanel() {
        return initiativeListPanel;
    }

    public CharacterDetailsPanel getCharacterDetailsPanel() {
        return characterDetailsPanel;
    }

    public MainMenuBar getMainMenuBar() {
        return menuBar;
    }

    public CharacterOptionsPanel getOptionsBarPanel() {
        return characterOptionsPanel;
    }
}
