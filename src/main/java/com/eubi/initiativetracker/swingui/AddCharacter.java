package com.eubi.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import java.awt.*;

import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;

public class AddCharacter extends JFrame {
    private JPanel addCharacterPane;
    private JTextField characterName;
    private JComboBox characterTypeCombo;
    private JSpinner initiativeSpinner;
    private JSpinner dexteritySpinner;
    private JButton addCharacterButton;
    private JComboBox deathStatusCombo;

    public AddCharacter(CharacterManager characterManager) {
        setContentPane(addCharacterPane);

        setTitle("Add Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        dexteritySpinner.setModel(new SpinnerNumberModel(0, 0, 30, 1));
        addCharacterButton.setEnabled(false);

        characterName.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                validateName();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                validateName();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                validateName();
            }
        });

        addCharacterButton.addActionListener(e -> {
            saveCharacter(characterManager);
        });
    }

    private void saveCharacter(CharacterManager characterManager) {
        String name = characterName.getText().trim();
        int initiative =  (int) initiativeSpinner.getValue();
        int dexterity = (int) dexteritySpinner.getValue();
        String type = (String) characterTypeCombo.getSelectedItem();
        String status = (String) deathStatusCombo.getSelectedItem();

        Character newCharacter = new Character(name, initiative, dexterity, type, status);

        characterManager.addCharacter(newCharacter);

    }

    private void validateName() {
        boolean isValid = !characterName.getText().trim().isEmpty();
        addCharacterButton.setEnabled(isValid);
    }
}
