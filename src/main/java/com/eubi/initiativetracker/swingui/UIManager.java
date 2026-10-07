package com.eubi.initiativetracker.swingui;

import com.eubi.initiativetracker.characters.CharacterManager;
import com.eubi.initiativetracker.characters.Character;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIManager {
    private CharacterManager characterManager;
    private JFrame parentFrame;
    private Map<String, Character> characterMap;
    private DefaultListModel<Character> characterListModel;
    private JButton passSaveButton;
    private JButton failSaveButton;
    private JLabel deathSaveLabel;

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

    public void openEditCharacterMenu() {
        updateCharacterMap();

        EditCharacterMenu editCharacterMenu = new EditCharacterMenu(parentFrame, characterMap);
        editCharacterMenu.setVisible(true);

        characterManager.updateCharacters();
        updateInitiativeList();
    }

    public void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterMenu removeCharacterMenu = new RemoveCharacterMenu(parentFrame, characterManager);
        removeCharacterMenu.setVisible(true);

        updateInitiativeList();
    }

    private void updateCharacterMap() {
        if  (!characterMap.isEmpty()) {characterMap.clear();}
        for (Character character : characterManager.getCharacterList()) {
            characterMap.put(character.getCharacterData().get("Name").toString(), character);
        }
    }

    private void updateInitiativeList() {
        characterListModel.clear();
        for (Character character : characterManager.getInitiativeOrder()) {
            characterListModel.addElement(character);
        }

        updateDeathSaveButtons();
    }

    public void nextTurn() {
        characterManager.newTurn();
        updateInitiativeList();
    }

    public void passDeathSave(boolean passed) {
        Character currentCharacter = characterListModel.get(0);
        currentCharacter.deathSave(passed);
    }

    private void updateDeathSaveButtons() {
        passSaveButton.setVisible(false);
        failSaveButton.setVisible(false);
        deathSaveLabel.setVisible(false);

        Character currentCharacter = characterListModel.get(0);

        if (currentCharacter.getDeathStatus().toString().equals("DOWN")) {
            passSaveButton.setVisible(true);
            failSaveButton.setVisible(true);
            deathSaveLabel.setVisible(true);
        }

        parentFrame.revalidate();
    }
}
