package com.eubi.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

public class EditNPCHealth extends JDialog {

    private final JPanel editNPCHealth = new JPanel();
    private final JComboBox<String> characterCombo = new JComboBox<>();
    private final JLabel nameLabel = new JLabel();
    private final JLabel currentHealth = new JLabel();
    private final JLabel newHealth = new JLabel();
    private final JSpinner changeValueSpinner = new JSpinner();
    private final JButton saveEditButton = new JButton("Save Edit");
    private final JLabel saveEditLabel = new JLabel("Saved Edit");

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

        changeValueSpinner.addChangeListener(e -> {
            int health = Integer.parseInt(currentHealth.getText()) + (int) changeValueSpinner.getValue();
            newHealth.setText(String.valueOf(health));
        });
        characterCombo.addActionListener(e -> updateCharactersDetails());
        saveEditButton.addActionListener(e -> saveNPCHealth());
    }

    private void setupMenu() {
        Dimension windowSize = new Dimension(250, 183);
        editNPCHealth.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][l][r, grow, push]", "[][][][][][][]"));
        editNPCHealth.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(editNPCHealth);

        final JLabel npcToEditLabel = new JLabel("NPC To Edit:");
        final JSeparator separator1 = new JSeparator();
        final JLabel characterNameLabel = new JLabel("Name:");
        final JLabel changeLabel = new JLabel("Change (-/+):");
        final JLabel differenceLabel = new JLabel("Difference:");

        saveEditButton.setFocusPainted(false);

        editNPCHealth.add(npcToEditLabel, "cell 0 0");
        editNPCHealth.add(separator1, "cell 0 1, span, growx");
        editNPCHealth.add(characterNameLabel, "cell 0 2");
        editNPCHealth.add(changeLabel, "cell 0 3");
        editNPCHealth.add(differenceLabel, "cell 0 4");
        editNPCHealth.add(saveEditLabel, "cell 0 6, span, growx, hidemode 3");

        editNPCHealth.add(nameLabel, "cell 1 2");
        editNPCHealth.add(currentHealth, "cell 1 4");

        editNPCHealth.add(characterCombo, "cell 2 0, growx");
        editNPCHealth.add(changeValueSpinner, "cell 2 3, growx");
        editNPCHealth.add(newHealth, "cell 2 4, growx");
        editNPCHealth.add(saveEditButton, "cell 2 5, growx");
    }

    private void setCharacterDetails() {
        saveEditLabel.setVisible(false);

        if (npcMap.isEmpty()) {
            changeValueSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 1));
            characterCombo.addItem("No Characters");
            nameLabel.setText("Null");
            currentHealth.setText("Null");
            newHealth.setText("Null");
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

        if (!this.npcMap.isEmpty()) {
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
        int maxChangeAmount = maxHealth - health;
        int minChangeAmount = -health;
        changeValueSpinner.setModel(new SpinnerNumberModel(0, minChangeAmount, maxChangeAmount, 1));

        nameLabel.setText(currentCharacterData.get("Name").toString());
        currentHealth.setText(String.valueOf(health));
        newHealth.setText(String.valueOf(health + (int) changeValueSpinner.getValue()));

    }

    private NonePlayerCharacter getCurrentCharacter() {
        if (characterCombo.getSelectedItem() == null || npcMap.isEmpty()) {
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
        int health = (int) changeValueSpinner.getValue();
        assert currentCharacter != null;
        currentCharacter.editHealth(health);

        saveEditLabel.setVisible(true);
        pack();
        updateCharactersDetails();
    }
}



