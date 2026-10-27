package com.c0d3wan.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

import com.c0d3wan.initiativetracker.characters.*;
import net.miginfocom.swing.MigLayout;

/**
 * Modal {@link JDialog} interface for editing the health of an existing NPC
 */
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

    private final Map<String, NonePlayerCharacter> npcMap;

    /**
     * Constructs a modal dialog for editing health of existing NPCs
     *
     * @param parent        The parent {@link Frame} window binding this dialog.
     * @param characterMap  Lookup map containing all registered characters keyed by name.
     */
    public EditNPCHealth(Frame parent, Map<String, NonePlayerCharacter> characterMap) {
        super(parent, "Edit NPC Health", true);
        npcMap = new HashMap<>(characterMap);
        setupMenu();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        setCharacterDetails();

        characterCombo.addActionListener(e -> updateCharactersDetails());
        saveEditButton.addActionListener(e -> saveNPCHealth());
    }

    /**
     * Sets up the Layout manager, layout, widgets and border for the {@link Frame}
     */
    private void setupMenu() {
        editNPCHealth.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][l][r, grow, push]", "[][][][][][][]"));
        editNPCHealth.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

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

    /**
     * Populates the {@link JComboBox} to hold the NPC names or "No Characters"
     */
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
            List<String> charactersToCombo = new ArrayList<>();
            List<String> hordesToRemove = new ArrayList<>();
            Map<String, NonePlayerCharacter> npcsToHash = new HashMap<>();
            for (Map.Entry<String, NonePlayerCharacter> entry : npcMap.entrySet()) {
                String characterName = entry.getKey();
                NonePlayerCharacter npc = entry.getValue();

                if (npc.getCharacterType() == CharacterType.HORDE) {
                    hordesToRemove.add(characterName);
                    List<NonePlayerCharacter> npcs =  ((Horde) npc).getNPCs();
                    for (NonePlayerCharacter enemy: npcs) {
                        charactersToCombo.add(enemy.getName());
                        npcsToHash.put(enemy.getName(), enemy);
                    }
                } else {
                    charactersToCombo.add(characterName);
                }
            }
            hordesToRemove.forEach(npcMap::remove);
            npcMap.putAll(npcsToHash);
            charactersToCombo.forEach(characterCombo::addItem);

            displayCurrentData();
        }
    }

    /**
     * Refreshes the {@link JLabel}s to hold the NPC's details
     */
    private void updateCharactersDetails() {
        saveEditLabel.setVisible(false);

        if (!npcMap.isEmpty()) {
            displayCurrentData();
        }
    }

    /**
     * Populates the {@link JLabel} and {@link JSpinner} with NPC current values
     */
    private void displayCurrentData() {
        NonePlayerCharacter currentCharacter = getCurrentCharacter();
        if (currentCharacter == null) {
            return;
        }

        int health = currentCharacter.getHealth();
        int maxHealth = currentCharacter.getMaxHealth();

        SpinnerNumberModel spinnerConstraints = new SpinnerNumberModel(0, 0, maxHealth, 1);

        minusButton.setSelected(false);
        plusButton.setSelected(true);
        nameLabel.setText(currentCharacter.getName());
        changeValueSpinner.setModel(spinnerConstraints);
        currentHealth.setText(health +"/"+ maxHealth);

        revalidate();
        repaint();
    }

    /**
     * Gets the current {@link NonePlayerCharacter} from the {@link JComboBox}
     * @return the currently selected {@link NonePlayerCharacter}
     */
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

    /**
     * Saves the edited NPC health to the current {@link NonePlayerCharacter}
     */
    private void saveNPCHealth() {
        NonePlayerCharacter currentCharacter = getCurrentCharacter();
        int health = (int) changeValueSpinner.getValue();
        if (minusButton.isSelected()) {
            health = -health;
        }

        if (currentCharacter == null) {
            return;
        }
        currentCharacter.editHealth(health);

        saveEditLabel.setVisible(true);
        updateCharactersDetails();
    }
}



