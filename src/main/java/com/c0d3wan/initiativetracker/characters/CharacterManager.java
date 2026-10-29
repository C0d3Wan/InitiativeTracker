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

    /**
     * Constructs a new, empty {@code CharacterManager}.
     */
    public CharacterManager() {
        characters = new ArrayList<>();
        charactersOrdered = new ArrayList<>();
    }

    /**
     * Adds a character to the combat tracker, sorts the initiative order,
     * and initializes the first turn if no combatant is currently active.
     *
     * @param character The {@link Character} to enrol.
     */
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

    /**
     * Advances combat to the next eligible character's turn.
     * <p>
     * Rotates cyclically through the initiative list, automatically skipping any
     * combatants whose status is {@link DeathStatus#DEAD}.
     */
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

    /**
     * Retrieves the remaining turn order for the current round, cyclically shifted
     * so that the active turn holder appears first.
     *
     * @return A rotated {@link List} of {@link Character} objects starting with the current turn.
     */
    public List<Character> getTurnOrder() {
        int i = charactersOrdered.indexOf(currentCharacter);
        if (i <= 0) return new ArrayList<>(charactersOrdered);

        List<Character> rotated = new ArrayList<>(charactersOrdered.subList(i, charactersOrdered.size()));
        rotated.addAll(charactersOrdered.subList(0, i));
        return rotated;
    }

    /**
     * Clears all registered combatants, sorted queues, and resets the active turn holder.
     */
    public void clearCharacters() {
        characters.clear();
        charactersOrdered.clear();
        currentCharacter = null;
    }

    /**
     * Sorts the registered characters in descending order according to their calculated initiative value.
     */
    public void sortCharacters() {
        charactersOrdered = characters.stream()
                .sorted(Comparator.comparingDouble(Character::getInitiativeValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Selects and sets the active turn holder by searching for a matching character name.
     *
     * @param name The name of the character to activate. Does nothing if {@code name} is null or not found.
     */
    public void setCurrentCharacterByName(String name) {
        if (name == null) return;
        for (Character c : characters) {
            if (c.getName().equals(name)) {
                currentCharacter = c;
                break;
            }
        }
    }

    /**
     * Provides an unmodifiable view of the raw character list in registration order.
     *
     * @return An unmodifiable {@link List} of registered characters.
     */
    public List<Character> getCharacterList() {
        return Collections.unmodifiableList(characters);
    }

    /**
     * Retrieves the active initiative order rotated from the current turn holder.
     *
     * @return A rotated {@link List} of {@link Character} objects.
     * @see #getTurnOrder()
     */
    public List<Character> getInitiativeOrder() {
        return getTurnOrder();
    }

    /**
     * Gets the character who is currently taking their turn.
     *
     * @return The active {@link Character}, or {@code null} if the tracker is empty.
     */
    public Character getCurrentCharacter() {
        return currentCharacter;
    }
}
