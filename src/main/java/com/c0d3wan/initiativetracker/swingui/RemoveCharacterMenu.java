package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.CharacterManager;
import com.c0d3wan.initiativetracker.characters.CharacterType;
import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Modal dialogue interface for selecting and removing single or all characters from tracking.
 */
public class RemoveCharacterMenu extends JDialog {
    private final JPanel removeCharacterPane = new JPanel();
    private final JComboBox<String> characterSelectionCombo = new JComboBox<>();
    private final JButton removeCharacterButton = new JButton("Remove Character");
    private final JLabel removedCharacterLabel = new JLabel("Character(s) Removed!!!");

    private final Map<String, Character> characterMap;
    private final Map<String, NonePlayerCharacter> npcMap;
    private final CharacterManager characterManager;

    /**
     * Constructs a modal dialogue to remove characters from the initiative tracker.
     *
     * @param parent           The parent {@link Frame} window.
     * @param characterManager The {@link CharacterManager} maintaining combatants.
     */
    public RemoveCharacterMenu(Frame parent, CharacterManager characterManager, Map<String, Character> characterMap, Map<String, NonePlayerCharacter> npcMap) {
        super(parent, "Remove Character", true);
        this.characterManager = characterManager;
        this.characterMap = new HashMap<>(characterMap);
        this.npcMap = new HashMap<>(npcMap);

        setupMenu();

        setTitle("Remove Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);

        updateSelectionCombo();

        removeCharacterButton.addActionListener(e -> {
            if (confirmRemove()) {
                removeCharacter();
            }
        });
    }
    /**
     * Sets up the Layout manager, layout, widgets and border for the {@link Frame}
     */
    private void setupMenu() {
        Dimension windowSize = new Dimension(275, 111);
        removeCharacterPane.setLayout(new MigLayout("insets 2 2 2 2, fillx", "[l][r, grow, push]", "[][][][]"));
        removeCharacterPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        setMinimumSize(windowSize);

        setContentPane(removeCharacterPane);

        final JLabel selectLabel = new JLabel("Select Character:");
        final JSeparator separator1 = new JSeparator();

        final DefaultComboBoxModel<String> selectionComboModel = new DefaultComboBoxModel<>();
        characterSelectionCombo.setModel(selectionComboModel);

        removedCharacterLabel.setVisible(false);

        removeCharacterPane.add(selectLabel, "cell 0 0");
        removeCharacterPane.add(separator1, "cell 0 1, span, growx");
        removeCharacterPane.add(removedCharacterLabel, "cell 0 3, span, hidemode 3, align center");

        removeCharacterPane.add(characterSelectionCombo, "cell 1 0, growx");
        removeCharacterPane.add(removeCharacterButton, "cell 1 2, growx");

    }

    /**
     * Reloads the selection drop-down list with all currently registered character names
     * as well as an "All Characters" option.
     */
    private void updateSelectionCombo() {
        List<Character> characters = this.characterManager.getCharacterList();
        characterSelectionCombo.removeAllItems();
        if (characters.isEmpty()) {
            characterSelectionCombo.addItem("No Characters");
            removeCharacterButton.setEnabled(false);
        } else {
            characterSelectionCombo.addItem("All Characters");
            characterSelectionCombo.addItem("All NPCs");

            removeCharacterButton.setEnabled(true);
            Set<String> enemyTypes = new HashSet<>();
            for (Character character : characters) {
                String name = character.getName();
                CharacterType type = character.getCharacterType();
                if (type != CharacterType.PLAYER) {
                    String enemyType = ((NonePlayerCharacter) character).getEnemyType();
                    if (!enemyType.isEmpty()) {
                        enemyTypes.add(enemyType);
                    }
                }
                characterSelectionCombo.addItem(name);
            }
            for (String type: enemyTypes) {
                characterSelectionCombo.insertItemAt(("All " + type), 2);
            }
        }
    }

    /**
     * Executes the removal of the selected character (or all characters) from the manager
     * and refreshes the selection menu.
     */
    private void removeCharacter() {
        Object selectedItem = characterSelectionCombo.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        String selection = selectedItem.toString();

        if (selection.equals("All Characters")) {
            characterManager.clearCharacters();
        } else if (selection.equals("All NPCs")) {
            for (NonePlayerCharacter npc: npcMap.values()) {
                characterManager.removeCharacter(npc);
            }
        } else if (selection.startsWith("All ")) {
            String enemyType = selection.substring("All ".length());

            for (NonePlayerCharacter npc: npcMap.values()) {
                if (enemyType.equals(npc.getEnemyType())) {
                    characterManager.removeCharacter(npc);
                }
            }
        } else {
            Character character = characterMap.get(selection);

            if (character != null) {
                characterManager.removeCharacter(character);
            } else {
                NonePlayerCharacter npc = npcMap.get(selection);

                if (npc != null) {
                    characterManager.removeCharacter(npc);
                }
            }
        }

        characterManager.sortCharacters();
        updateSelectionCombo();
        removedCharacterLabel.setVisible(true);
    }

    /**
     * Confirms if a user wants to remove the selected character
     * @return {@link Boolean}
     */
    private boolean confirmRemove() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove character(s)",
                "Remove Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
}
