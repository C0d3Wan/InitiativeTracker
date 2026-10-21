package com.c0d3wan.initiativetracker.swingui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.*;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

/**
 * Modal dialog interface for creating and adding a new character to the initiative tracker.
 */
public class AddCharacterMenu extends JDialog {
    private final JPanel addCharacterPane = new JPanel();
    private final JTextField characterName = new JTextField();
    private final JSpinner initiativeSpinner = new JSpinner();
    private final JSpinner dexteritySpinner= new JSpinner();
    private final JSpinner maxHealthSpinner = new JSpinner();
    private final JSpinner npcAmountSpinner = new JSpinner();
    private final JComboBox<String> deathStatusCombo = new JComboBox<>();
    private final JComboBox<String> characterTypeCombo = new JComboBox<>();
    private final JButton addCharacterButton = new JButton("Add character");
    private final JLabel npcAmountLabel = new JLabel("NPC Amount:");
    private final JLabel maxHealthLabel = new JLabel("Max Health:");
    private final JLabel savedCharacterLabel = new JLabel("Character Saved!!!");

    private boolean confirmed = false;
    private final List<Character> characters = new ArrayList<>();
    private final Map<String, Character> characterMap;

    /**
     * Constructs a new modal dialog to create a character.
     *
     * @param parent       The parent {@link Frame} window binding this dialog.
     * @param characterMap The existing map of characters used to prevent duplicate names.
     */
    public AddCharacterMenu(Frame parent, Map<String, Character> characterMap) {
        super(parent, "Add Character", true);
        this.characterMap = characterMap;
        setupMenu();

        setTitle("Add Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        pack();

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

        characterTypeCombo.addActionListener(e -> updateForMaxHealth());
        addCharacterButton.addActionListener(e -> saveCharacter());
    }

    private void saveCharacter() {
        String name = characterName.getText().trim();
        int initiative = (int) initiativeSpinner.getValue();
        int dexterity = (int) dexteritySpinner.getValue();
        String type = (String) characterTypeCombo.getSelectedItem();
        String status = (String) deathStatusCombo.getSelectedItem();

        if (name.length() > 16) {
            name = name.substring(0, 14) + "...";
        }

        assert type != null;
        Character newCharacter;
        if (!type.equalsIgnoreCase("PLAYER")) {
            int maxHealth = (int) maxHealthSpinner.getValue();
            int maxNPCS = (int) npcAmountSpinner.getValue();
            for (int i = 1; i <= maxNPCS; i++) {
                String newName = name + " " + i;
                newCharacter = new NonePlayerCharacter(newName, initiative, dexterity, maxHealth, type, status);
                if (maxNPCS > 1) {
                    ((NonePlayerCharacter) newCharacter).editEnemyType(name);
                }
                characterMap.put(name, newCharacter);
                characters.add(newCharacter);
            }
        } else {
            newCharacter = new Character(name, initiative, dexterity, type, status);

            characterMap.put(name, newCharacter);
            characters.add(newCharacter);
        }
        confirmed = true;
        savedCharacterLabel.setVisible(true);
        resetWidgets();
        pack();
    }

    private void setupMenu() {
        Dimension windowSize = new Dimension(400,220);
        addCharacterPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][center, grow, push][right, grow]", "[][][][][][][][][]"));
        addCharacterPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(addCharacterPane);

        final JLabel characterNameLabel = new JLabel("Character Name:");
        final JLabel initiativeLabel = new JLabel("Initiative:");
        final JLabel dexterityLabel = new JLabel("Dexterity (0-30):");
        final JLabel characterTypeLabel = new JLabel("Character Type:");
        final JLabel deathStatusLabel = new JLabel("Death Status");

        initiativeSpinner.setModel(new SpinnerNumberModel(0, -30, 200, 1));
        dexteritySpinner.setModel(new SpinnerNumberModel(0, 0, 30, 1));
        maxHealthSpinner.setModel(new SpinnerNumberModel(1, 1, 2000, 1));
        npcAmountSpinner.setModel(new SpinnerNumberModel(1, 1, 30, 1));

        final DefaultComboBoxModel<String> characterTypeComboModel = new DefaultComboBoxModel<>();
        characterTypeComboModel.addElement("PLAYER");
        characterTypeComboModel.addElement("ENEMY");
        characterTypeComboModel.addElement("ALLY");
        characterTypeCombo.setModel(characterTypeComboModel);

        final DefaultComboBoxModel<String> deathStatusComboModel = new DefaultComboBoxModel<>();
        deathStatusComboModel.addElement("ALIVE");
        deathStatusComboModel.addElement("DEAD");
        deathStatusComboModel.addElement("DOWN");
        deathStatusCombo.setModel(deathStatusComboModel);

        addCharacterButton.setFocusPainted(false);
        addCharacterButton.setEnabled(false);
        maxHealthLabel.setVisible(false);
        maxHealthSpinner.setVisible(false);
        savedCharacterLabel.setVisible(false);
        npcAmountLabel.setVisible(false);
        npcAmountSpinner.setVisible(false);

        addCharacterPane.add(characterNameLabel, "cell 0 0");
        addCharacterPane.add(initiativeLabel, "cell 0 1");
        addCharacterPane.add(dexterityLabel, "Cell 0 2");
        addCharacterPane.add(characterTypeLabel, "cell 0 3");
        addCharacterPane.add(maxHealthLabel, "cell 0 4, hidemode 3");
        addCharacterPane.add(npcAmountLabel, "cell 0 5, hidemode 3");
        addCharacterPane.add(deathStatusLabel, "cell 0 6");

        addCharacterPane.add(characterName, "cell 1 0, span, growx");
        addCharacterPane.add(initiativeSpinner, "cell 1 1, span, growx");
        addCharacterPane.add(dexteritySpinner, "cell 1 2, span, growx");
        addCharacterPane.add(characterTypeCombo, "cell 1 3, span, growx");
        addCharacterPane.add(maxHealthSpinner, "cell 1 4, hidemode 3, span, growx");
        addCharacterPane.add(npcAmountSpinner, "cell 1 5, hidemode 3, span, growx");
        addCharacterPane.add(deathStatusCombo, "cell 1 6, span, growx");

        addCharacterPane.add(addCharacterButton, "cell 2 7, align right, growx");
        addCharacterPane.add(savedCharacterLabel, "cell 0 8, span, growx, hidemode 3");
    }

    /**
     * Checks that the entered name is non-empty and case-insensitively unique against existing characters.
     * Enables or disables the submit button accordingly.
     */
    private void validateName() {
        String name = characterName.getText().trim();
        boolean isValid = !name.isEmpty();
        for (String characterName : characterMap.keySet()) {
            if (name.equalsIgnoreCase(characterName)) {
                isValid = false;
                break;
            }
        }

        addCharacterButton.setEnabled(isValid);
    }

    private void updateForMaxHealth() {
        Object selectedItem = characterTypeCombo.getSelectedItem();
        assert selectedItem != null;
        if (!selectedItem.equals("PLAYER")) {
            maxHealthLabel.setVisible(true);
            maxHealthSpinner.setVisible(true);
            npcAmountSpinner.setVisible(true);
            npcAmountLabel.setVisible(true);
        } else {
            maxHealthLabel.setVisible(false);
            maxHealthSpinner.setVisible(false);
            npcAmountSpinner.setVisible(false);
            npcAmountLabel.setVisible(false);
        }
        pack();
    }

    /**
     * Checks if the creation form was submitted successfully.
     *
     * @return {@code true} if confirmed; {@code false} if canceled or closed.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Retrieves the newly created character instance.
     *
     * @return The configured {@link Character}, or {@code null} if unconfirmed.
     */
    public List<Character> getNewCharacter() {
        return characters;
    }

    private void resetWidgets() {
        characterName.setText("");
        initiativeSpinner.setValue(0);
        dexteritySpinner.setValue(0);
        characterTypeCombo.setSelectedIndex(0);
        maxHealthSpinner.setValue(1);
        deathStatusCombo.setSelectedIndex(0);
    }
}
