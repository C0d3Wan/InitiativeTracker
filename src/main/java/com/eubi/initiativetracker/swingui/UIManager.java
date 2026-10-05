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

    public UIManager(JFrame parentFrame, CharacterManager characterManager, JList<Character> initiativeList) {
        this.characterManager = characterManager;
        this.parentFrame = parentFrame;
        characterMap = new HashMap<>();

    }

    public void openAddCharacterMenu() {
        AddCharacterMenu addCharacterMenu = new AddCharacterMenu(parentFrame);
        addCharacterMenu.setVisible(true);

        if (addCharacterMenu.isConfirmed()) {
            Character newCharacter = addCharacterMenu.getNewCharacter();
            characterManager.addCharacter(newCharacter);
            characterManager.sortCharacters();
            updateCharacterMap();
        }
    }

    public void openEditCharacterMenu() {
        List<Character> characters = characterManager.getCharacterVector();

        EditCharacterMenu editCharacterMenu = new EditCharacterMenu(parentFrame, characterMap);
        editCharacterMenu.setVisible(true);


        characterManager.sortCharacters();
    }

    public void openRemoveCharacterMenu() {
        RemoveCharacterMenu removeCharacterMenu = new RemoveCharacterMenu(parentFrame, characterManager);
        removeCharacterMenu.setVisible(true);


    }

    private void updateCharacterMap() {
        if  (!characterMap.isEmpty()) {characterMap.clear();}
        for (Character character : characterManager.getCharacterVector()) {
            characterMap.put(character.getCharacterData().get("Name").toString(), character);
        }
    }


}
