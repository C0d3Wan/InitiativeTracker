package com.c0d3wan.initiativetracker.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.c0d3wan.initiativetracker.filemanagement.saveload.ConfigManager;
import com.c0d3wan.initiativetracker.ui.components.CharacterDetailsPanel;
import com.c0d3wan.initiativetracker.ui.components.DeathSavesPanel;
import com.c0d3wan.initiativetracker.ui.components.InitiativeListPanel;
import com.c0d3wan.initiativetracker.ui.components.MainMenuBar;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Top-level application window hosting the main initiative tracker user interface.
 */
public class MainWindow extends JFrame {
    private final JPanel mainMenuPane = new JPanel();
    private final JButton addCharacterButton = new JButton("Add Character");
    private final JButton nextTurnButton = new JButton("Next Turn");
    private final JButton removeCharacterButton = new JButton("Remove Character");
    private final JButton editCharacterButton = new JButton("Edit Character");
    private final JButton editNPCHealthButton = new JButton("Edit NPC Health");

    private final MainMenuBar menuBar;
    private final InitiativeListPanel initiativeListPanel;
    private final CharacterDetailsPanel characterDetailsPanel;
    private final DeathSavesPanel deathSavesPanel;

    /**
     * Constructs and initialises the main application window.
     */
    public MainWindow() {
        initiativeListPanel = new InitiativeListPanel();
        characterDetailsPanel = new CharacterDetailsPanel();
        deathSavesPanel = new DeathSavesPanel();
        menuBar = new MainMenuBar();

        //uiController.openBestiaryMenu();
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
        Dimension windowSize = new Dimension(550, 300);
        mainMenuPane.setLayout(new MigLayout("insets 2 0 2 0, fill", "[center][center][center][center]", "[center][center][center][center][center]"));
        mainMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        addCharacterButton.setFocusPainted(false);
        editCharacterButton.setFocusPainted(false);
        editNPCHealthButton.setFocusPainted(false);
        removeCharacterButton.setFocusPainted(false);

        final JSeparator separator1 = new JSeparator();
        final JLabel label1 = new JLabel("Initiative Order");

        nextTurnButton.setFocusPainted(false);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, initiativeListPanel, characterDetailsPanel);
        characterDetailsPanel.setVisible(false);

        mainMenuPane.add(addCharacterButton, "cell 0 0, growx");
        mainMenuPane.add(editCharacterButton, "cell 1 0, growx");
        mainMenuPane.add(editNPCHealthButton, "cell 2 0, growx");
        mainMenuPane.add(removeCharacterButton, "cell 3 0, growx");

        mainMenuPane.add(separator1, "cell 0 1, span, growx");
        mainMenuPane.add(label1, "cell 0 2, span");

        mainMenuPane.add(nextTurnButton, "cell 0 3, growx");
        mainMenuPane.add(deathSavesPanel, "cell 1 3, align right, span");

        mainMenuPane.add(splitPane, "cell 0 4, span, grow, pushy");

        deathSavesPanel.setVisible(false);
    }

    public void addAddCharacterListener(ActionListener listener) {
        addCharacterButton.addActionListener(listener);
    }

    public void addEditCharacterListener(ActionListener listener) {
        editCharacterButton.addActionListener(listener);
    }

    public void addEditNpcListener(ActionListener listener) {
        editNPCHealthButton.addActionListener(listener);
    }

    public void addRemoveCharacterListener(ActionListener listener) {
        removeCharacterButton.addActionListener(listener);
    }

    public void addNextTurnListener(ActionListener listener) {
        nextTurnButton.addActionListener(listener);
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

    public DeathSavesPanel getDeathSavesPanel() {
        return deathSavesPanel;
    }

    public MainMenuBar getMainMenuBar() {
        return menuBar;
    }
}
