package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.Character;
import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.NonePlayerCharacter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Modal dialog interface for selecting and removing single or all characters from tracking.
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
     * Constructs a modal dialog to remove characters from the initiative tracker.
     *
     * @param parent           The parent {@link Frame} window.
     * @param characterManager The {@link CharacterManager} maintaining combatants.
     */
    public RemoveCharacterMenu(Frame parent, CharacterManager characterManager, Map<String, Character> characterMap, Map<String, NonePlayerCharacter> npcMap) {
        super(parent, "Remove Character", true);
        this.characterManager = characterManager;
        this.characterMap= characterMap;
        this.npcMap = npcMap;

        setupMenu();

        setTitle("Remove Character");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        updateSelectionCombo();

        removeCharacterButton.addActionListener(e -> removeCharacter());
    }

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
        removeCharacterPane.add(removedCharacterLabel, "cell 0 3, span, growx, hidemode 3");

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
            removeCharacterButton.setEnabled(true);
            for (Character character : characters) {
                String name = character.getCharacterData().get("Name").toString();
                characterSelectionCombo.addItem(name);
            }
            characterSelectionCombo.addItem("All NPCs");
            characterSelectionCombo.addItem("All Characters");
        }
        pack();
    }

    /**
     * Executes the removal of the selected character (or all characters) from the manager
     * and refreshes the selection menu.
     */
    private void removeCharacter() {
        Object selectedItem = characterSelectionCombo.getSelectedItem();
        assert selectedItem != null;
        String name = selectedItem.toString();
        switch (name) {
            case "All NPCs" -> {
                for (NonePlayerCharacter npc: npcMap.values()) {
                    characterManager.removeCharacter(npc);
                    characterManager.sortCharacters();
                }
            }
            case "All Characters" -> characterManager.clearCharacters();
            default -> {
                Character character = characterMap.get(name);
                characterManager.removeCharacter(character);
                characterManager.sortCharacters();
            }
        }

        updateSelectionCombo();
        removedCharacterLabel.setVisible(true);
        pack();
    }
}
