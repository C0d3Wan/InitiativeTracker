package com.c0d3wan.initiativetracker.characters;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the collection of active combatants, handles descending initiative sorting,
 * tracks the active turn holder, and rotates turn order cyclically.
 */
public class CharacterManager {
    private final List<Character> characters;
    private List<Character> charactersOrdered;
    private Character currentCharacter;

    public CharacterManager() {
        characters = new ArrayList<>();
        charactersOrdered = new ArrayList<>();
    }

    public void addCharacter(Character character) {
        characters.add(character);
        sortCharacters();

        if (currentCharacter == null) {
            newTurn();
        }

    }

    /**
     * Refreshes the descending sorted order and updates the initiative queue.
     * <p>
     * Typically invoked following batch attribute edits (such as initiative or status changes).
     */
    public void updateCharacters() {
        sortCharacters();
    }

    /**
     * Removes an active character from combat and advances turn order smoothly if necessary.
     * <p>
     * If the character being removed is currently taking their turn, active status advances
     * to the next eligible character in the cycle.
     *
     * @param character The exact {@link Character} reference to remove.
     */
    public void removeCharacter(Character character) {
        sortCharacters();
        boolean wasCurrent = (character == currentCharacter);
        int index = charactersOrdered.indexOf(character);

        characters.remove(character);
        sortCharacters();


        if (!wasCurrent) {
            return;
        }

        currentCharacter = null;
        if (!charactersOrdered.isEmpty()) {
            currentCharacter = charactersOrdered.get(index % charactersOrdered.size());
        }

    }

    public void newTurn() {
        sortCharacters();
        if (charactersOrdered.isEmpty()) {
            currentCharacter = null;
            return;
        }

        int nextIndex = 0;
        if (currentCharacter != null) {
            int currentIndex = charactersOrdered.indexOf(currentCharacter);
            nextIndex = (currentIndex + 1) % charactersOrdered.size();
        }

        currentCharacter = charactersOrdered.get(nextIndex);

        int checked = 0;
        while (currentCharacter.getDeathStatus() == DeathStatus.DEAD && checked < charactersOrdered.size()) {
            int currentIndex = charactersOrdered.indexOf(currentCharacter);
            nextIndex = (currentIndex + 1) % charactersOrdered.size();
            currentCharacter = charactersOrdered.get(nextIndex);
            checked++;
        }


    }

    public List<Character> getTurnOrder() {
        int i = charactersOrdered.indexOf(currentCharacter);
        if (i <= 0) return new ArrayList<>(charactersOrdered);

        List<Character> rotated = new ArrayList<>(charactersOrdered.subList(i, charactersOrdered.size()));
        rotated.addAll(charactersOrdered.subList(0, i));
        return rotated;
    }

    public void clearCharacters() {
        characters.clear();
        charactersOrdered.clear();
        currentCharacter = null;
    }

    public void sortCharacters() {
        charactersOrdered = characters.stream()
                .sorted(Comparator.comparingDouble(Character::getInitiativeValue).reversed())
                .collect(Collectors.toList());
    }

    public void setCurrentCharacterByName(String name) {
        if (name == null) return;
        for (Character c : characters) {
            if (c.getName().equals(name)) {
                currentCharacter = c;
                break;
            }
        }
    }

    public List<Character> getCharacterList() {
        return Collections.unmodifiableList(characters);
    }

    public List<Character> getInitiativeOrder() {
        return getTurnOrder();
    }

    public Character getCurrentCharacter() {
        return currentCharacter;
    }
}
