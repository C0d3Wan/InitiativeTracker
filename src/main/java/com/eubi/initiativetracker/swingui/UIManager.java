package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.Character;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class coordinating interactions between the character tracking data layer
 * and the Swing graphical user interface components.
 */
public class UIManager {
    private final CharacterManager characterManager;
    private final JFrame parentFrame;
    private final Map<String, Character> characterMap;
    private final DefaultListModel<Character> characterListModel;
    private final JButton passSaveButton;
    private final JButton failSaveButton;
    private final JLabel deathSaveLabel;

    /**
     * Constructs a new UIManager instance and attaches views and cell renderers.
     *
     * @param parentFrame      The primary {@link JFrame} hosting the interface.
     * @param characterManager The {@link CharacterManager} providing domain logic.
     * @param initiativeList   The {@link JList} visual component displaying initiative order.
     * @param passSaveButton   The {@link JButton} triggered when a death save succeeds.
     * @param failSaveButton   The {@link JButton} triggered when a death save fails.
     * @param deathSaveLabel   The {@link JLabel} displayed when death saves are applicable.
     */
    public UIManager(JFrame parentFrame, CharacterManager characterManager, JList<Character> initiativeList, JButton passSaveButton, JButton failSaveButton, JLabel deathSaveLabel) {
        this.characterManager = characterManager;
        this.parentFrame = parentFrame;
        characterMap = new HashMap<>();
        characterListModel = new DefaultListModel<>();
        this.passSaveButton = passSaveButton;
        this.failSaveButton = failSaveButton;
        this.deathSaveLabel = deathSaveLabel;

        initiativeList.setModel(characterListModel);
        initiativeList.setCellRenderer(new CharacterCellRenderer());
    }

    /**
     * Opens the modal dialog to create and add a new character.
     * Refreshes the initiative list if the user confirms creation.
     */
    public void openAddCharacterMenu() {
        List<Character> characters = characterManager.getCharacterList();
        AddCharacterMenu addCharacterMenu = new AddCharacterMenu(parentFrame, characters);
        addCharacterMenu.setVisible(true);

        if (addCharacterMenu.isConfirmed()) {
            Character newCharacter = addCharacterMenu.getNewCharacter();
            characterManager.addCharacter(newCharacter);
            updateCharacterMap();

            updateInitiativeList();
        }
    }

    /**
     * Opens the modal dialog to modify attributes of an existing character.
     * Refreshes the initiative queue when changes are saved.
     */
    public void openEditCharacterMenu() {
        updateCharacterMap();

        EditCharacterMenu editCharacterMenu = new EditCharacterMenu(parentFrame, characterMap);
        editCharacterMenu.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialog to remove selected or all characters from the combat session.
     */
    public void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterMenu removeCharacterMenu = new RemoveCharacterMenu(parentFrame, characterManager);
        removeCharacterMenu.setVisible(true);

        updateInitiativeList();
    }

    /**
     * Synchronizes the internal name-to-character lookup table with the character manager.
     */
    private void updateCharacterMap() {
        if  (!characterMap.isEmpty()) {characterMap.clear();}
        for (Character character : characterManager.getCharacterList()) {
            characterMap.put(character.getCharacterData().get("Name").toString(), character);
        }
    }

    /**
     * Clears and repopulates the initiative list model from the manager's active order,
     * subsequently updating the death save control buttons.
     */
    private void updateInitiativeList() {
        characterListModel.clear();
        for (Character character : characterManager.getInitiativeOrder()) {
            characterListModel.addElement(character);
        }

        updateDeathSaveButtons();
    }

    /**
     * Advances the game to the next participant's turn and repaints the initiative order.
     */
    public void nextTurn() {
        characterManager.newTurn();
        updateInitiativeList();
    }

    /**
     * Records a death saving throw for the active combatant at the top of the turn list.
     *
     * @param passed {@code true} to record a success; {@code false} to record a failure.
     */
    public void passDeathSave(boolean passed) {
        if (characterListModel.isEmpty()) {
            return;
        }

        Character currentCharacter = characterListModel.get(0);
        currentCharacter.deathSave(passed);
        updateInitiativeList();
    }

    /**
     * Adjusts the visibility and text of death save UI controls based on whether
     * the active character at the top of the initiative order is downed.
     */
    private void updateDeathSaveButtons() {
        passSaveButton.setVisible(false);
        failSaveButton.setVisible(false);
        deathSaveLabel.setVisible(false);

        if (characterListModel.isEmpty()) {
            parentFrame.revalidate();
            parentFrame.repaint();
            return;
        }

        Character currentCharacter = characterListModel.get(0);

        if (currentCharacter.getDeathStatus().toString().equals("DOWN")) {
            passSaveButton.setVisible(true);
            failSaveButton.setVisible(true);
            deathSaveLabel.setVisible(true);
        }

        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
