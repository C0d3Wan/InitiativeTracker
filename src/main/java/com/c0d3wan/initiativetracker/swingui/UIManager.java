package com.c0d3wan.initiativetracker.swingui;

import com.c0d3wan.initiativetracker.FileManagement.SaveLoadManager;
import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.*;
import com.formdev.flatlaf.*;

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
    private final Map<String, Character> characterMap = new HashMap<>();
    private final Map<String, NonePlayerCharacter> npcMap = new HashMap<>();
    private final DefaultListModel<Character> characterListModel = new DefaultListModel<>();

    private JButton passSaveButton;
    private JButton failSaveButton;
    private JLabel deathSaveLabel;

    /**
     * Constructs a new UIManager instance and attaches views and cell renderers.
     *
     * @param parentFrame      The primary {@link JFrame} hosting the interface.
     * @param characterManager The {@link CharacterManager} providing domain logic.
     * @param initiativeList   The {@link JList} visual component displaying initiative order.
     */
    public UIManager(JFrame parentFrame, CharacterManager characterManager, JList<Character> initiativeList) {
        this.characterManager = characterManager;
        this.parentFrame = parentFrame;

        initiativeList.setModel(characterListModel);
        initiativeList.setCellRenderer(new CharacterCellRenderer());
    }

    /**
     * Adds the death save items to the uiManager
     * @param passSaveButton   The {@link JButton} triggered when a death save succeeds.
     * @param failSaveButton   The {@link JButton} triggered when a death save fails.
     * @param deathSaveLabel   The {@link JLabel} displayed when death saves are applicable.
     */
    public void shareDeathSaveItems(JButton passSaveButton, JButton failSaveButton, JLabel deathSaveLabel) {
        this.passSaveButton = passSaveButton;
        this.failSaveButton = failSaveButton;
        this.deathSaveLabel = deathSaveLabel;
    }

    /**
     * Opens the modal dialog to create and add a new character.
     * Refreshes the initiative list if the user confirms creation.
     */
    public void openAddCharacterMenu() {
        updateCharacterMap();
        AddCharacterMenu addCharacterMenu = new AddCharacterMenu(parentFrame, characterMap);
        addCharacterMenu.setVisible(true);

        if (addCharacterMenu.isConfirmed()) {
            List<Character> newCharacters = addCharacterMenu.getNewCharacter();
            for (Character character: newCharacters) {
                characterManager.addCharacter(character);
            }
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
     * Opens the modal dialog to modify existing NPC health values.
     */
    public void openEditNPCHealthMenu() {
        updateCharacterMap();

        EditNPCHealth editNPCHealthMenu = new EditNPCHealth(parentFrame, npcMap);
        editNPCHealthMenu.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialog to remove selected or all characters from the combat session.
     */
    public void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterMenu removeCharacterMenu = new RemoveCharacterMenu(parentFrame, characterManager, characterMap, npcMap);
        removeCharacterMenu.setVisible(true);

        updateInitiativeList();
    }

    /**
     * Synchronizes the internal name-to-character lookup table with the character manager.
     */
    private void updateCharacterMap() {
        characterMap.clear();
        npcMap.clear();
        for (Character character : characterManager.getCharacterList()) {
        if (character.getCharacterType() != CharacterType.PLAYER) {
                npcMap.put(character.getName(), (NonePlayerCharacter) character);
            } else {
                characterMap.put(character.getName(), character);
            }
        }
    }

    /**
     * Clears and repopulates the initiative list model from the manager's active order,
     * subsequently updating the death save control buttons.
     */
    private void updateInitiativeList() {
        characterListModel.clear();
        characterManager.getInitiativeOrder().forEach(characterListModel::addElement);

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
        if (passSaveButton == null ||
                failSaveButton == null ||
                deathSaveLabel == null) {
            return;
        }
        passSaveButton.setVisible(false);
        failSaveButton.setVisible(false);
        deathSaveLabel.setVisible(false);

        if (characterListModel.isEmpty()) {
            parentFrame.revalidate();
            parentFrame.repaint();
            return;
        }

        Character currentCharacter = characterListModel.get(0);

        if (currentCharacter.getDeathStatus() == DeathStatus.DOWN) {
            passSaveButton.setVisible(true);
            failSaveButton.setVisible(true);
            deathSaveLabel.setVisible(true);
        }

        parentFrame.revalidate();
        parentFrame.repaint();
    }

    /**
     * Changes the colour mode of the ui (Light/Dark)
     *
     * @param darkMode boolean {@code true} for dark mode, {@code false} for light mode
     */
    public static void setDarkMode(boolean darkMode) {
        if (darkMode) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
    }

    /**
     * Calls the {@link SaveLoadManager} to save the current Initiative list
     */
    public void saveList() {
        SaveLoadManager.save(characterManager);
    }

    /**
     * Opens a {@link SaveAsMenu} to save the current list to a new file
     */
    public void saveListAs() {
        SaveAsMenu saveAsMenu = new SaveAsMenu(parentFrame);
        saveAsMenu.setVisible(true);

        if (saveAsMenu.isConfirmed()) {
            String saveFile = saveAsMenu.getSavefile();
            SaveLoadManager.saveAs(characterManager, saveFile);
        }
    }

    /**
     * Opens a {@link LoadFileMenu} to load a list from an existing file
     */
    public void loadList() {
        LoadFileMenu loadMenu = new LoadFileMenu(parentFrame);
        loadMenu.setVisible(true);
        if (loadMenu.isItemSelected()) {
            String fileToLoad = loadMenu.getFileToLoad();
            SaveLoadManager.load(characterManager, fileToLoad, true);
            updateInitiativeList();
        }
    }
}
