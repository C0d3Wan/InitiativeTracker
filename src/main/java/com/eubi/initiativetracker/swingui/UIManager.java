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

    public UIManager(JFrame parentFrame, CharacterManager characterManager, JList<Character> initiativeList) {
        this.characterManager = characterManager;
        this.parentFrame = parentFrame;
        characterMap = new HashMap<>();

        CharacterCellRenderer characterRenderer = new CharacterCellRenderer();


    }

    public void openAddCharacterMenu() {
        List<Character> characters = characterManager.getCharacterList();
        AddCharacterMenu addCharacterMenu = new AddCharacterMenu(parentFrame, characters);
        addCharacterMenu.setVisible(true);

        if (addCharacterMenu.isConfirmed()) {
            Character newCharacter = addCharacterMenu.getNewCharacter();
            characterManager.addCharacter(newCharacter);
            characterManager.sortCharacters();
            updateCharacterMap();
        }
    }

    public void openEditCharacterMenu() {
        updateCharacterMap();

        EditCharacterMenu editCharacterMenu = new EditCharacterMenu(parentFrame, characterMap);
        editCharacterMenu.setVisible(true);


        characterManager.sortCharacters();
    }

    public void openRemoveCharacterMenu() {
        updateCharacterMap();
        RemoveCharacterMenu removeCharacterMenu = new RemoveCharacterMenu(parentFrame, characterManager);
        removeCharacterMenu.setVisible(true);


    }

    private void updateCharacterMap() {
        if  (!characterMap.isEmpty()) {characterMap.clear();}
        for (Character character : characterManager.getCharacterList()) {
            characterMap.put(character.getCharacterData().get("Name").toString(), character);
        }
    }


}
