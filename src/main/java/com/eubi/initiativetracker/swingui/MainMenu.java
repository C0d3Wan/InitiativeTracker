package com.eubi.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Top-level application window hosting the main initiative tracker user interface.
 */
public class MainMenu extends JFrame {
    private final JPanel mainMenuPane = new JPanel();
    private final JButton addCharacterButton = new JButton("Add Character");
    private final JButton nextTurnButton = new JButton("Next Turn");
    private final JButton removeCharacterButton = new JButton("Remove Character");
    private final JList<Character> initiativeList = new JList<>();
    private final JButton editCharacterButton = new JButton("Edit Character");
    private final JButton passButton = new JButton("Pass");
    private final JButton failButton = new JButton("Fail");
    private final JLabel deathSaveLabel = new JLabel("Death Saves:");
    private final JButton editNPCHealthButton = new JButton("Edit NPC Health");

    private final JMenuBar menuBar = new JMenuBar();

    private final UIManager uiManager;

    /**
     * Constructs and initializes the main application window.
     *
     * @param characterManager The {@link CharacterManager} maintaining combat data.
     */
    public MainMenu(CharacterManager characterManager) {
        uiManager = new UIManager(this, characterManager, initiativeList, passButton, failButton, deathSaveLabel);
        setupMainMenu();
        setupMenuBar();

        setJMenuBar(menuBar);
        setContentPane(mainMenuPane);
        setTitle("Initiative Tracker");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        addCharacterButton.addActionListener(e -> uiManager.openAddCharacterMenu());
        editCharacterButton.addActionListener(e -> uiManager.openEditCharacterMenu());
        editNPCHealthButton.addActionListener(e -> uiManager.openEditNPCHealthMenu());
        removeCharacterButton.addActionListener(e -> uiManager.openRemoveCharacterMenu());
        nextTurnButton.addActionListener(e -> uiManager.nextTurn());

        passButton.addActionListener(e -> uiManager.passDeathSave(true));
        failButton.addActionListener(e -> uiManager.passDeathSave(false));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void setupMainMenu() {
        Dimension windowSize = new Dimension(550, 300);
        mainMenuPane.setLayout(new MigLayout("insets 2 0 2 0, fillx", "[center][center][center][center]", "[center][center][center][center][center]"));
        mainMenuPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        addCharacterButton.setFocusPainted(false);
        editCharacterButton.setFocusPainted(false);
        editNPCHealthButton.setFocusPainted(false);
        removeCharacterButton.setFocusPainted(false);

        final JSeparator separator1 = new JSeparator();
        final JLabel label1 = new JLabel("Initiative Order");

        nextTurnButton.setFocusPainted(false);
        passButton.setFocusPainted(false);
        failButton.setFocusPainted(false);

        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setViewportView(initiativeList);


        mainMenuPane.add(addCharacterButton, "cell 0 0, growx");
        mainMenuPane.add(editCharacterButton, "cell 1 0, growx");
        mainMenuPane.add(editNPCHealthButton, "cell 2 0, growx");
        mainMenuPane.add(removeCharacterButton, "cell 3 0, growx");

        mainMenuPane.add(separator1, "cell 0 1, span, growx");
        mainMenuPane.add(label1, "cell 0 2, span");

        mainMenuPane.add(nextTurnButton, "cell 0 3, growx");
        mainMenuPane.add(deathSaveLabel, "cell 1 3, align right");
        mainMenuPane.add(passButton, "cell 2 3, growx");
        mainMenuPane.add(failButton, "cell 3 3, growx");

        mainMenuPane.add(scrollPane1, "cell 0 4, span, grow, pushy");

        deathSaveLabel.setVisible(false);
        passButton.setVisible(false);
        failButton.setVisible(false);
    }

    private void setupMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        menuBar.add(viewMenu);
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Tracker?",
                "Exit Conformation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
