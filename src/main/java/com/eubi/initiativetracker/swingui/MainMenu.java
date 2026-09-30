package com.eubi.initiativetracker.swingui;

import javax.swing.*;
import com.eubi.initiativetracker.characters.CharacterManager;

public class MainMenu extends JFrame{
    private JPanel mainMenuPane;
    private JButton addCharacterButton;
    private JButton nextTurnButton;
    private JButton removeCharacterButton;
    private JList initiativeList;
    private JButton editCharacterButton;

    public MainMenu(CharacterManager characterManager) {
        setContentPane(mainMenuPane);

        setTitle("Initiative Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        addCharacterButton.addActionListener(e -> {
            AddCharacter addCharacter = new AddCharacter(characterManager);
            addCharacter.setVisible(true);
        });

        editCharacterButton.addActionListener(e -> {

        });

        removeCharacterButton.addActionListener(e -> {

        });
    }
}
