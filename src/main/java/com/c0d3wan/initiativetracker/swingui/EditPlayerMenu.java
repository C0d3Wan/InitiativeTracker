package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * Modal {@link JDialog} interface for editing statistics (initiative, dexterity, life status)
 * of an existing character.
 */
public class EditPlayerMenu extends JDialog {
    private final JPanel editPlayerPane = new JPanel();
    private final JLabel characterNameLabel = new JLabel();
    private final JSpinner newInitiativeSpinner = new JSpinner();
    private final JLabel oldInitiativeLabel = new JLabel();
    private final JLabel oldDexterityLabel = new JLabel();
    private final JLabel oldDeathStatusLabel = new JLabel();
    private final JSpinner newDexteritySpinner = new JSpinner();
    private final JComboBox<DeathStatus> newDeathStatusCombo = new JComboBox<>();
    private final JComboBox<String> charactersCombo = new JComboBox<>();
    private final JButton saveEditButton = new JButton("Save Edit");
    private final JLabel savedPlayerLabel = new JLabel("Player Saved!!");

    private final Map<String, Character> playerMap;

    /**
     * Constructs a modal dialogue to modify attributes of existing players.
     *
     * @param parent       The parent {@link Frame} window binding this dialogue.
     * @param characterMap Lookup map containing all registered characters keyed by name.
     */
    public EditPlayerMenu(Frame parent, Map<String, Character> characterMap) {
        super(parent, "Edit Character", true);
        this.playerMap = characterMap;
        setupMenu();

        setTitle("Edit Character");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        updateCharactersCombo();

        charactersCombo.addActionListener(e -> updateDetails());
        saveEditButton.addActionListener(e -> saveEditedPlayer());
    }

    /**
     * Sets up the Layout manager, layout, widgets and border for the {@link Frame}
     */
    private void setupMenu() {
        Dimension windowSize = new Dimension(400, 220);
        editPlayerPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][l][r, grow, push]", "[][][][][][][][]"));
        editPlayerPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(editPlayerPane);

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

        final DefaultComboBoxModel<DeathStatus> deathStatusComboModel = new DefaultComboBoxModel<>();
        deathStatusComboModel.addElement(DeathStatus.ALIVE);
        deathStatusComboModel.addElement(DeathStatus.DEAD);
        deathStatusComboModel.addElement(DeathStatus.DOWN);
        newDeathStatusCombo.setModel(deathStatusComboModel);

        saveEditButton.setFocusPainted(false);
        savedPlayerLabel.setVisible(false);

        editPlayerPane.add(characterEditLabel, "cell 0 0, span, growx");
        editPlayerPane.add(separator1, "cell 0 1, span, growx");
        editPlayerPane.add(nameLabel, "cell 0 2, span, growx");
        editPlayerPane.add(initiativeLabel, "cell 0 3");
        editPlayerPane.add(dexterityLabel, "cell 0 4");
        editPlayerPane.add(deathStatusLabel, "cell 0 5");
        editPlayerPane.add(savedPlayerLabel, "cell 0 7, span, hidemode 3, align center");

        editPlayerPane.add(characterNameLabel, "cell 1 2");
        editPlayerPane.add(oldInitiativeLabel, "cell 1 3");
        editPlayerPane.add(oldDexterityLabel, "cell 1 4");
        editPlayerPane.add(oldDeathStatusLabel, "cell 1 5");

        editPlayerPane.add(charactersCombo, "cell 2 0, growx");
        editPlayerPane.add(newInitiativeSpinner, "cell 2 3, growx");
        editPlayerPane.add(newDexteritySpinner, "cell 2 4, growx");
        editPlayerPane.add(newDeathStatusCombo, "cell 2 5, growx");
        editPlayerPane.add(saveEditButton, "cell 2 6, growx");
    }

    /**
     * Reads form fields and commits modified initiative, dexterity, and death status
     * back to the selected character instance.
     */
    private void saveEditedPlayer() {
        Character currentCharacter = getCurrentCharacter();
        if (currentCharacter == null) {
            return;
        }

        CharacterData currentCharacterData = currentCharacter.getCharacterData();
        int newDexterity = (int) newDexteritySpinner.getValue();
        int newInitiative = (int) newInitiativeSpinner.getValue();
        Object selectedItem = newDeathStatusCombo.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        String newDeathStatus = selectedItem.toString();

        if (newDexterity != currentCharacterData.dexterity()) {
            currentCharacter.setDexterity(newDexterity);
        }
        if (newInitiative != currentCharacterData.initiative()) {
            currentCharacter.setInitiative(newInitiative);
        }
        if (newDeathStatus == null) {return;}
        if (!newDeathStatus.equals(currentCharacterData.deathStatus().toString())) {
            currentCharacter.setDeathStatus(newDeathStatus);
        }
        updateDetails();
        savedPlayerLabel.setVisible(true);

        //this.dispose();
    }

    /**
     * Populates the character selection {@link JComboBox} with registered characters
     * and initialises selection state.
     */
    private void updateCharactersCombo() {

        savedPlayerLabel.setVisible(false);

        if (playerMap.isEmpty()) {
            charactersCombo.addItem("No Characters");
            characterNameLabel.setText("Null");
            oldInitiativeLabel.setText("Null");
            oldDexterityLabel.setText("Null");
            oldDeathStatusLabel.setText("Null");
            saveEditButton.setEnabled(false);
        } else {
            saveEditButton.setEnabled(true);
            for (String characterName : playerMap.keySet()) {
                charactersCombo.removeAllItems();
                charactersCombo.addItem(characterName);
            }

            displayCurrentCharacterData();
        }
    }

    /**
     * Refreshes the display view with attributes from the currently selected character in the combo box.
     */
    private void updateDetails() {
        savedPlayerLabel.setVisible(false);

        if (!playerMap.isEmpty()) {
            displayCurrentCharacterData();
        }
    }

    /**
     * Populates the label indicators and spinner fields with the active character's current values.
     */
    private void displayCurrentCharacterData() {
        Character currentCharacter = getCurrentCharacter();
        if (currentCharacter == null) {return;}
        CharacterData currentCharacterData = currentCharacter.getCharacterData();
        characterNameLabel.setText(currentCharacter.getName());
        oldInitiativeLabel.setText(String.valueOf(currentCharacterData.initiative()));
        oldDexterityLabel.setText(String.valueOf(currentCharacterData.dexterity()));
        oldDeathStatusLabel.setText(currentCharacter.getDeathStatus().toString());

        newInitiativeSpinner.setValue(currentCharacterData.initiative());
        newDexteritySpinner.setValue(currentCharacterData.dexterity());
        newDeathStatusCombo.setSelectedItem(currentCharacter.getDeathStatus());
    }

    /**
     * Finds and returns the character currently selected in the dropdown combo box.
     *
     * @return The active {@link Character} chosen in the combo box.
     */
    private Character getCurrentCharacter() {
        if (charactersCombo.getSelectedItem() == null || playerMap.isEmpty()) {
            if (!playerMap.isEmpty()) {
                return playerMap.entrySet().iterator().next().getValue();
            }
            return null;
        }
        String name = charactersCombo.getSelectedItem().toString();
        return playerMap.get(name);
    }
}
