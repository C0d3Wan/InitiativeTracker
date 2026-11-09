package com.c0d3wan.initiativetracker.ui;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.c0d3wan.initiativetracker.filemanagement.saveload.ConfigManager;
import com.c0d3wan.initiativetracker.filemanagement.saveload.SaveLoadManager;
import com.c0d3wan.initiativetracker.characters.Character;
import com.c0d3wan.initiativetracker.characters.*;
import com.c0d3wan.initiativetracker.characters.data.CharacterType;
import com.c0d3wan.initiativetracker.characters.data.DeathStatus;
import com.c0d3wan.initiativetracker.ui.bestiary.BestiaryMenu;
import com.c0d3wan.initiativetracker.ui.components.CharacterDetailsPanel;
import com.c0d3wan.initiativetracker.ui.components.DeathSavesPanel;
import com.c0d3wan.initiativetracker.ui.components.InitiativeListPanel;
import com.c0d3wan.initiativetracker.ui.components.MainMenuBar;
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
    private final MainWindow mainWindow;
    private final Map<String, Character> characterMap = new HashMap<>();
    private final Map<String, NonePlayerCharacter> npcMap = new HashMap<>();

    private final InitiativeListPanel initiativeListPanel;
    private final CharacterDetailsPanel characterDetailsPanel;
    private final DeathSavesPanel deathSavesPanel;
    private final MainMenuBar menuBar;

    /**
     * Constructs a new UIManager instance and attaches views and cell renderers.
     *
     * @param parentFrame      The primary {@link JFrame} hosting the interface.
     * @param characterManager The {@link CharacterManager} providing domain logic.
     */
    public UIController(MainWindow parentFrame, CharacterManager characterManager) {
        this.characterManager = characterManager;
        mainWindow = parentFrame;
        initiativeListPanel = mainWindow.getInitiativeListPanel();
        characterDetailsPanel = mainWindow.getCharacterDetailsPanel();
        deathSavesPanel = mainWindow.getDeathSavesPanel();
        menuBar = mainWindow.getMainMenuBar();

        initialiseListeners();
    }

    /**
     * prompts the user to ensure they want to exit the application
     */
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                mainWindow,
                "Are you sure you want to exit the Tracker?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            ConfigManager.saveConfig();
            System.exit(0);
        }
    }

    private void initialiseListeners() {
        initiativeListPanel.addSelectionListener(e -> {
            Character selectedCharacter = initiativeListPanel.getSelectedCharacter();
        });
        deathSavesPanel.addPassListener(e -> passDeathSave(true));
        deathSavesPanel.addFailListener(e -> passDeathSave(false));

        menuBar.addLightModeListener(e -> {
            setDarkMode(false);
            menuBar.enableDarkButton(false);
        });
        menuBar.addDarkModeListener(e -> {
            setDarkMode(true);
            menuBar.enableDarkButton(true);
        });
        menuBar.addOpenItemListener(e -> loadList());
        menuBar.addSaveItemListener(e -> saveList());
        menuBar.addSaveAsItemListener(e -> saveListAs());
        menuBar.addExitListener(e -> confirmExit());

        mainWindow.addAddCharacterListener(e -> openAddCharacterMenu());
        mainWindow.addEditCharacterListener(e -> openEditCharacterMenu());
        mainWindow.addEditNpcListener(e -> openEditNPCHealthMenu());
        mainWindow.addRemoveCharacterListener(e -> openRemoveCharacterMenu());
        mainWindow.addNextTurnListener(e -> nextTurn());
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
     * Adjusts the visibility and text of death save UI controls based on whether
     * the active character at the top of the initiative order is downed.
     */
    private void updateDeathSaveButtons() {
        if (deathSavesPanel == null) {
            return;
        }
        deathSavesPanel.setVisible(false);

        Character currentCharacter = initiativeListPanel.getCurrentCharacter();

        if (currentCharacter == null) {
            mainWindow.revalidate();
            mainWindow.repaint();
            return;
        }

        if (currentCharacter.getDeathStatus() == DeathStatus.DOWN) {
            deathSavesPanel.setVisible(true);
        }

        mainWindow.revalidate();
        mainWindow.repaint();
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
     * Opens the modal dialogue to create and add a new character.
     * Refreshes the initiative list if the user confirms creation.
     */
    private void openAddCharacterMenu() {
        updateCharacterMap();
        AddCharacterDialog addCharacterDialog = new AddCharacterDialog(mainWindow, characterMap);
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
    private void openEditCharacterMenu() {
        updateCharacterMap();

        EditPlayerDialog editPlayerDialog = new EditPlayerDialog(mainWindow, characterMap);
        editPlayerDialog.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to modify existing NPC health values.
     */
    private void openEditNPCHealthMenu() {
        updateCharacterMap();

        EditNPCDialog editNPCDialog = new EditNPCDialog(mainWindow, npcMap);
        editNPCDialog.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to remove selected or all characters from the combat session.
     */
    private void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterDialog removeCharacterDialog = new RemoveCharacterDialog(mainWindow, characterManager, characterMap, npcMap);
        removeCharacterDialog.setVisible(true);

        updateInitiativeList();
    }

    /**
     * Opens the modal dialogue to browse the bestiary database and add monsters to tracker.
     */
    private void openBestiaryMenu() {
        BestiaryMenu bestiaryMenu = new BestiaryMenu(mainWindow, characterManager);
        bestiaryMenu.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    /**
     * Advances the game to the next participant's turn and repaints the initiative order.
     */
    private void nextTurn() {
        characterManager.newTurn();
        updateInitiativeList();
    }

    /**
     * Records a death saving throw for the active combatant at the top of the turn list.
     *
     * @param passed {@code true} to record a success; {@code false} to record a failure.
     */
    private void passDeathSave(boolean passed) {
        Character currentCharacter = initiativeListPanel.getCurrentCharacter();
        if (currentCharacter != null) {
            currentCharacter.deathSave(passed);
            updateInitiativeList();
        }
    }

    /**
     * Calls the {@link SaveLoadManager} to save the current Initiative list
     */
    private void saveList() {
        SaveLoadManager.save(characterManager);
    }

    /**
     * Opens a {@link JFileChooser} to save the current list to a new file
     */
    private void saveListAs() {

        JFileChooser saveAsMenu = createFileMenu();
        int isConfirmed = saveAsMenu.showSaveDialog(mainWindow);

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
    private void loadList() {
        JFileChooser loadMenu = createFileMenu();
        int isConfirmed = loadMenu.showOpenDialog(mainWindow);

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
}