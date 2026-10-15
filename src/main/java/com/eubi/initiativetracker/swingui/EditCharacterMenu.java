package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * Modal dialog interface for editing statistics (initiative, dexterity, life status)
 * of an existing character.
 */
public class EditCharacterMenu extends JDialog {
    private JPanel editCharacterPane;
    private JLabel characterNameLabel;
    private JSpinner newInitiativeSpinner;
    private JLabel initiativeLabel;
    private JLabel dexterityLabel;
    private JLabel deathStatusLabel;
    private JSpinner newDexteritySpinner;
    private JComboBox<String> newDeathStatusCombo;
    private JComboBox<String> charactersCombo;
    private JButton saveEditButton;
    private JLabel savedCharacterLabel;

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

        setContentPane(editCharacterPane);
        setTitle("Edit Character");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        updateCharactersCombo();

        charactersCombo.addActionListener(e -> updateDetails());
        saveEditButton.addActionListener(e -> saveEditedCharacter());
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
        newDexteritySpinner.setModel(new SpinnerNumberModel(0, 0, 30, 1));
        newInitiativeSpinner.setModel(new SpinnerNumberModel(0, -30, 200, 1));
        savedCharacterLabel.setVisible(false);

        if (characterMap.isEmpty()) {
            charactersCombo.addItem("No Characters");
            characterNameLabel.setText("Null");
            initiativeLabel.setText("Null");
            dexterityLabel.setText("Null");
            deathStatusLabel.setText("Null");
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
        initiativeLabel.setText(currentCharacterData.get("Initiative").toString());
        dexterityLabel.setText(currentCharacterData.get("Dexterity").toString());
        deathStatusLabel.setText(currentCharacterData.get("DeathStatus").toString());

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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        editCharacterPane = new JPanel();
        editCharacterPane.setLayout(new GridBagLayout());
        editCharacterPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Name");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Initiative");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Dexterity");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Death Status");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(label4, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(separator1, gbc);
        characterNameLabel = new JLabel();
        characterNameLabel.setEnabled(true);
        characterNameLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(characterNameLabel, gbc);
        newInitiativeSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(newInitiativeSpinner, gbc);
        initiativeLabel = new JLabel();
        initiativeLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(initiativeLabel, gbc);
        dexterityLabel = new JLabel();
        dexterityLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(dexterityLabel, gbc);
        deathStatusLabel = new JLabel();
        deathStatusLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        editCharacterPane.add(deathStatusLabel, gbc);
        newDexteritySpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(newDexteritySpinner, gbc);
        newDeathStatusCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("ALIVE");
        defaultComboBoxModel1.addElement("DEAD");
        defaultComboBoxModel1.addElement("DOWN");
        newDeathStatusCombo.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(newDeathStatusCombo, gbc);
        charactersCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        charactersCombo.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(charactersCombo, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Character to Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 0);
        editCharacterPane.add(label5, gbc);
        saveEditButton = new JButton();
        saveEditButton.setText("Save Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        editCharacterPane.add(saveEditButton, gbc);
        savedCharacterLabel = new JLabel();
        savedCharacterLabel.setText("Character Saved!!");
        savedCharacterLabel.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 0, 0);
        editCharacterPane.add(savedCharacterLabel, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        editCharacterPane.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        editCharacterPane.add(spacer2, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return editCharacterPane;
    }

}
