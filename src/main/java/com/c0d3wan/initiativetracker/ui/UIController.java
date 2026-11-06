package com.c0d3wan.initiativetracker.ui;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.c0d3wan.initiativetracker.filemanagement.saveload.SaveLoadManager;
import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.*;
import com.c0d3wan.initiativetracker.characters.data.CharacterType;
import com.c0d3wan.initiativetracker.characters.data.DeathStatus;
import com.c0d3wan.initiativetracker.ui.bestiary.BestiaryMenu;
import com.c0d3wan.initiativetracker.ui.components.InitiativeListPanel;
import com.c0d3wan.initiativetracker.ui.dialogs.AddCharacterDialog;
import com.c0d3wan.initiativetracker.ui.dialogs.EditNPCDialog;
import com.c0d3wan.initiativetracker.ui.dialogs.EditPlayerDialog;
import com.c0d3wan.initiativetracker.ui.dialogs.RemoveCharacterDialog;
import com.formdev.flatlaf.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class coordinating interactions between the character tracking data layer
 * and the Swing graphical user interface components.
 */
public class UIController {
    private final CharacterManager characterManager;
    private final MainWindow parentFrame;
    private final Map<String, Character> characterMap = new HashMap<>();
    private final Map<String, NonePlayerCharacter> npcMap = new HashMap<>();
    private final InitiativeListPanel initiativeListPanel;

    private JButton passSaveButton;
    private JButton failSaveButton;
    private JLabel deathSaveLabel;

    /**
     * Constructs a new UIManager instance and attaches views and cell renderers.
     *
     * @param parentFrame      The primary {@link JFrame} hosting the interface.
     * @param characterManager The {@link CharacterManager} providing domain logic.
     * @param initiativeListPanel   The {@link JList} visual component displaying initiative order.
     */
    public UIController(MainWindow parentFrame, CharacterManager characterManager, InitiativeListPanel initiativeListPanel) {
        this.characterManager = characterManager;
        this.parentFrame = parentFrame;
        this.initiativeListPanel = initiativeListPanel;
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
     * Opens the modal dialogue to create and add a new character.
     * Refreshes the initiative list if the user confirms creation.
     */
    public void openAddCharacterMenu() {
        updateCharacterMap();
        AddCharacterDialog addCharacterDialog = new AddCharacterDialog(parentFrame, characterMap);
        addCharacterDialog.setVisible(true);

        if (addCharacterDialog.isConfirmed()) {
            List<Character> newCharacters = addCharacterDialog.getNewCharacter();
            for (Character character: newCharacters) {
                characterManager.addCharacter(character);
            }
            updateCharacterMap();

            updateInitiativeList();
        }
    }

    /**
     * Opens the modal dialogue to modify attributes of an existing character.
     * Refreshes the initiative queue when changes are saved.
     */
    public void openEditCharacterMenu() {
        updateCharacterMap();

        EditPlayerDialog editPlayerDialog = new EditPlayerDialog(parentFrame, characterMap);
        editPlayerDialog.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to modify existing NPC health values.
     */
    public void openEditNPCHealthMenu() {
        updateCharacterMap();

        EditNPCDialog editNPCDialog = new EditNPCDialog(parentFrame, npcMap);
        editNPCDialog.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to remove selected or all characters from the combat session.
     */
    public void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterDialog removeCharacterDialog = new RemoveCharacterDialog(parentFrame, characterManager, characterMap, npcMap);
        removeCharacterDialog.setVisible(true);

        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to browse the bestiary database and add monsters to tracker.
     */
    public void openBestiaryMenu() {
        BestiaryMenu bestiaryMenu = new BestiaryMenu(parentFrame, characterManager);
        bestiaryMenu.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Synchronises the internal name-to-character lookup table with the character manager.
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
        initiativeListPanel.refreshCharacters(characterManager.getInitiativeOrder());

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
        Character currentCharacter = initiativeListPanel.getCurrentCharacter();
        if (currentCharacter != null) {
            currentCharacter.deathSave(passed);
            updateInitiativeList();
        }
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

        Character currentCharacter = initiativeListPanel.getCurrentCharacter();

        if (currentCharacter == null) {
            parentFrame.revalidate();
            parentFrame.repaint();
            return;
        }

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
     * Opens a {@link JFileChooser} to save the current list to a new file
     */
    public void saveListAs() {

        JFileChooser saveAsMenu = createFileMenu();
        int isConfirmed = saveAsMenu.showSaveDialog(parentFrame);

        if (isConfirmed == JFileChooser.APPROVE_OPTION) {
            File file = saveAsMenu.getSelectedFile();
            if (!file.getName().endsWith(".sav")){
                file = new File(file + ".sav");
            }
            SaveLoadManager.saveAs(characterManager, file);
        }
    }

    /**
     * Opens a {@link JFileChooser} to load a list from an existing file
     */
    public void loadList() {
        JFileChooser loadMenu = createFileMenu();
        int isConfirmed = loadMenu.showOpenDialog(parentFrame);

        if (isConfirmed == JFileChooser.APPROVE_OPTION) {
            File file = loadMenu.getSelectedFile();
            SaveLoadManager.load(characterManager, file, true);
            updateInitiativeList();
        }
    }

    private static JFileChooser createFileMenu() {
        JFileChooser fileMenu = new JFileChooser();
        fileMenu.setFileHidingEnabled(false);
        fileMenu.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String filename = file.getName().toLowerCase();
                    return filename.endsWith(".sav");
                }
            }

            @Override
            public String getDescription() {
                return "Save Files (*.sav)";
            }
        });
        fileMenu.setCurrentDirectory(FileManager.getSaveDirectory());
        return fileMenu;
    }
}