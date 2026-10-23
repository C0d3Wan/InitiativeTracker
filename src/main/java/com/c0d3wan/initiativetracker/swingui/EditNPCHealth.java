package com.c0d3wan.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

public class EditNPCHealth extends JDialog {

    private final JPanel editNPCHealth = new JPanel();
    private final JComboBox<String> characterCombo = new JComboBox<>();
    private final JLabel nameLabel = new JLabel();
    private final JLabel currentHealth = new JLabel();
    private final JSpinner changeValueSpinner = new JSpinner();
    private final JButton saveEditButton = new JButton("Save Edit");
    private final JLabel saveEditLabel = new JLabel("Saved Edit");
    private final JRadioButton plusButton = new JRadioButton("+");
    private final JRadioButton minusButton = new JRadioButton("-");

    private final Map<String, NonePlayerCharacter> npcMap = new HashMap<>();

    public EditNPCHealth(Frame parent, Map<String, Character> characterMap) {
        super(parent, "Edit NPC Health", true);
        setupMenu();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        for (Character character : characterMap.values()) {
            Map<String, Object> characterData = character.getCharacterData();
            if (!characterData.get("Type").toString().equals("PLAYER")) {
                npcMap.put(characterData.get("Name").toString(), (NonePlayerCharacter) character);
            }
        }

        setCharacterDetails();

        characterCombo.addActionListener(e -> updateCharactersDetails());
        saveEditButton.addActionListener(e -> saveNPCHealth());
    }

    private void setupMenu() {
        Dimension windowSize = new Dimension(300, 183);
        editNPCHealth.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][l][r, grow, push]", "[][][][][][][]"));
        editNPCHealth.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(editNPCHealth);

        ButtonGroup values = new ButtonGroup();
        values.add(plusButton);
        values.add(minusButton);

        final JLabel npcToEditLabel = new JLabel("NPC To Edit:");
        final JSeparator separator1 = new JSeparator();
        final JLabel characterNameLabel = new JLabel("Name:");
        final JLabel changeLabel = new JLabel("Change (+/-):");
        final JLabel currentHealthLabel = new JLabel("Current Health:");

        saveEditButton.setFocusPainted(false);

        editNPCHealth.add(npcToEditLabel, "cell 0 0");
        editNPCHealth.add(separator1, "cell 0 1, span, growx");
        editNPCHealth.add(characterNameLabel, "cell 0 2");
        editNPCHealth.add(currentHealthLabel, "cell 0 3");
        editNPCHealth.add(changeLabel, "cell 0 4");
        editNPCHealth.add(saveEditLabel, "cell 0 6, span, hidemode 3, align center");

        editNPCHealth.add(plusButton, "cell 1 4, split 2");
        editNPCHealth.add(minusButton, "cell 1 4");

        editNPCHealth.add(characterCombo, "cell 2 0, growx");
        editNPCHealth.add(nameLabel, "cell 2 2");
        editNPCHealth.add(currentHealth, "cell 2 3");
        editNPCHealth.add(changeValueSpinner, "cell 2 4, growx");
        editNPCHealth.add(saveEditButton, "cell 2 5, growx");
    }

    private void setCharacterDetails() {
        saveEditLabel.setVisible(false);

        if (npcMap.isEmpty()) {
            changeValueSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 1));
            characterCombo.addItem("No Characters");
            nameLabel.setText("Null");
            currentHealth.setText("Null");
            saveEditButton.setEnabled(false);
        } else {
            saveEditButton.setEnabled(true);
            for (String characterName : npcMap.keySet()) {
                characterCombo.addItem(characterName);
            }

            displayCurrentData();
        }
        pack();
    }

    private void updateCharactersDetails() {
        saveEditLabel.setVisible(false);

        if (!npcMap.isEmpty()) {
            displayCurrentData();
            pack();
        }
    }

    private void displayCurrentData() {
        NonePlayerCharacter currentCharacter = getCurrentCharacter();
        assert currentCharacter != null;
        Map<String, Object> currentCharacterData = currentCharacter.getCharacterData();

        int health = currentCharacter.getHealth();
        int maxHealth = currentCharacter.getMaxHealth();

        SpinnerNumberModel spinnerConstraints = new SpinnerNumberModel(0, 0, maxHealth, 1);

        minusButton.setSelected(false);
        plusButton.setSelected(true);
        nameLabel.setText(currentCharacterData.get("Name").toString());
        changeValueSpinner.setModel(spinnerConstraints);
        currentHealth.setText(health +"/"+ maxHealth);

        pack();
    }

    private NonePlayerCharacter getCurrentCharacter() {
        if (characterCombo.getSelectedItem() == null) {
            if (!npcMap.isEmpty()) {
                return npcMap.entrySet().iterator().next().getValue();
            }
            return null;
        }
        String name = characterCombo.getSelectedItem().toString();
        return npcMap.get(name);
    }

    private void saveNPCHealth() {
        NonePlayerCharacter currentCharacter = getCurrentCharacter();
        int health;
        if (minusButton.isSelected()) {
            health = -(int) changeValueSpinner.getValue();
        } else {
            health = (int) changeValueSpinner.getValue();
        }

        assert currentCharacter != null;
        currentCharacter.editHealth(health);

        saveEditLabel.setVisible(true);
        pack();
        updateCharactersDetails();
    }
}



