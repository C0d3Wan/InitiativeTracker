package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * Modal dialog interface for editing statistics (initiative, dexterity, life status)
 * of an existing character.
 */
public class EditCharacterMenu extends JDialog {
    private final JPanel editCharacterPane = new JPanel();
    private final JLabel characterNameLabel = new JLabel();
    private final JSpinner newInitiativeSpinner = new JSpinner();
    private final JLabel oldInitiativeLabel = new JLabel();
    private final JLabel oldDexterityLabel = new JLabel();
    private final JLabel oldDeathStatusLabel = new JLabel();
    private final JSpinner newDexteritySpinner = new JSpinner();
    private final JComboBox<String> newDeathStatusCombo = new JComboBox<>();
    private final JComboBox<String> charactersCombo = new JComboBox<>();
    private final JButton saveEditButton = new JButton("Save Edit");
    private final JLabel savedCharacterLabel = new JLabel("Character Saved!!");

    private final Map<String, Character> characterMap;

    /**
     * Constructs a modal dialog to modify attributes of existing characters.
     *
     * @param parent       The parent {@link Frame} window binding this dialog.
     * @param characterMap Lookup map containing all registered characters keyed by name.
     */
    public EditCharacterMenu(Frame parent, Map<String, Character> characterMap) {
        super(parent, "Edit Character", true);
        this.characterMap = characterMap;
        setupMenu();

        setTitle("Edit Character");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        updateCharactersCombo();

        charactersCombo.addActionListener(e -> updateDetails());
        saveEditButton.addActionListener(e -> saveEditedCharacter());
    }

    private void setupMenu() {
        Dimension windowSize = new Dimension(400, 220);
        editCharacterPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][l][r, grow, push]", "[][][][][][][][]"));
        editCharacterPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(editCharacterPane);

        final DefaultComboBoxModel<String> charactersComboModel = new DefaultComboBoxModel<>();
        charactersCombo.setModel(charactersComboModel);

        final JLabel characterEditLabel = new JLabel("Characters To Edit");
        final JSeparator separator1 = new JSeparator();
        final JLabel nameLabel = new JLabel("Name:");
        final JLabel initiativeLabel = new JLabel("Initiative:");
        final JLabel dexterityLabel = new JLabel("Dexterity:");
        final JLabel deathStatusLabel = new JLabel("Death Status:");

        newDexteritySpinner.setModel(new SpinnerNumberModel(0, 0, 30, 1));
        newInitiativeSpinner.setModel(new SpinnerNumberModel(0, -30, 200, 1));

        final DefaultComboBoxModel<String> deathStatusComboModel = new DefaultComboBoxModel<>();
        deathStatusComboModel.addElement("ALIVE");
        deathStatusComboModel.addElement("DEAD");
        deathStatusComboModel.addElement("DOWN");
        newDeathStatusCombo.setModel(deathStatusComboModel);

        saveEditButton.setFocusPainted(false);
        savedCharacterLabel.setVisible(false);

        editCharacterPane.add(characterEditLabel, "cell 0 0, span, growx");
        editCharacterPane.add(separator1, "cell 0 1, span, growx");
        editCharacterPane.add(nameLabel, "cell 0 2, span, growx");
        editCharacterPane.add(initiativeLabel, "cell 0 3");
        editCharacterPane.add(dexterityLabel, "cell 0 4");
        editCharacterPane.add(deathStatusLabel, "cell 0 5");
        editCharacterPane.add(savedCharacterLabel, "cell 0 7, span, growx, hidemode 3");

        editCharacterPane.add(characterNameLabel, "cell 1 2");
        editCharacterPane.add(oldInitiativeLabel, "cell 1 3");
        editCharacterPane.add(oldDexterityLabel, "cell 1 4");
        editCharacterPane.add(oldDeathStatusLabel, "cell 1 5");

        editCharacterPane.add(charactersCombo, "cell 2 0, growx");
        editCharacterPane.add(newInitiativeSpinner, "cell 2 3, growx");
        editCharacterPane.add(newDexteritySpinner, "cell 2 4, growx");
        editCharacterPane.add(newDeathStatusCombo, "cell 2 5, growx");
        editCharacterPane.add(saveEditButton, "cell 2 6, growx");
    }

    /**
     * Reads form fields and commits modified initiative, dexterity, and death status
     * back to the selected character instance.
     */
    private void saveEditedCharacter() {
        Character currentCharacter = getCurrentCharacter();
        if (currentCharacter == null) {
            return;
        }

        Map<String, Object> currentCharacterData = currentCharacter.getCharacterData();
        int newDexterity = (int) newDexteritySpinner.getValue();
        int newInitiative = (int) newInitiativeSpinner.getValue();
        String newDeathStatus = (String) newDeathStatusCombo.getSelectedItem();

        if (newDexterity != (int) currentCharacterData.get("Dexterity")) {
            currentCharacter.editDexValue(newDexterity);
        }
        if (newInitiative != (int) currentCharacterData.get("Initiative")) {
            currentCharacter.editInitiativeNum(newInitiative);
        }
        assert newDeathStatus != null;
        if (!newDeathStatus.equals(currentCharacterData.get("DeathStatus"))) {
            currentCharacter.editDeathStatus(newDeathStatus);
        }
        updateDetails();
        savedCharacterLabel.setVisible(true);
        pack();

        //this.dispose();
    }

    /**
     * Populates the character selection dropdown with registered characters
     * and initializes selection state.
     */
    private void updateCharactersCombo() {

        savedCharacterLabel.setVisible(false);

        if (characterMap.isEmpty()) {
            charactersCombo.addItem("No Characters");
            characterNameLabel.setText("Null");
            oldInitiativeLabel.setText("Null");
            oldDexterityLabel.setText("Null");
            oldDeathStatusLabel.setText("Null");
            saveEditButton.setEnabled(false);
        } else {
            saveEditButton.setEnabled(true);
            for (String characterName : characterMap.keySet()) {
                charactersCombo.addItem(characterName);
            }

            displayCurrentCharacterData();
        }
        pack();
    }

    /**
     * Refreshes the display view with attributes from the currently selected character in the combo box.
     */
    private void updateDetails() {
        savedCharacterLabel.setVisible(false);

        if (!this.characterMap.isEmpty()) {
            displayCurrentCharacterData();
            pack();
        }
    }

    /**
     * Populates the label indicators and spinner fields with the active character's current values.
     */
    private void displayCurrentCharacterData() {
        Character currentCharacter = getCurrentCharacter();
        assert currentCharacter != null;
        Map<String, Object> currentCharacterData = currentCharacter.getCharacterData();
        characterNameLabel.setText(currentCharacterData.get("Name").toString());
        oldInitiativeLabel.setText(currentCharacterData.get("Initiative").toString());
        oldDexterityLabel.setText(currentCharacterData.get("Dexterity").toString());
        oldDeathStatusLabel.setText(currentCharacterData.get("DeathStatus").toString());

        newInitiativeSpinner.setValue(currentCharacterData.get("Initiative"));
        newDexteritySpinner.setValue(currentCharacterData.get("Dexterity"));
        newDeathStatusCombo.setSelectedItem(currentCharacterData.get("DeathStatus"));
    }

    /**
     * Finds and returns the character currently selected in the dropdown combo box.
     *
     * @return The active {@link Character} chosen in the combo box.
     */
    private Character getCurrentCharacter() {
        if (charactersCombo.getSelectedItem() == null || characterMap.isEmpty()) {
            if (!characterMap.isEmpty()) {
                return characterMap.entrySet().iterator().next().getValue();
            }
            return null;
        }
        String name = charactersCombo.getSelectedItem().toString();
        return characterMap.get(name);
    }
}
