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
    private List<Character> initiativeOrdered;
    private Character currentCharacter;

    /**
     * Constructs a new, empty CharacterManager instance.
     */
    public CharacterManager() {
        characters = new ArrayList<>();
        charactersOrdered = new ArrayList<>();
        initiativeOrdered = new ArrayList<>();
    }

    /**
     * Registers a new character into combat, resorts the roster, and recalculates turn order.
     * <p>
     * If no turn is currently underway, this method automatically starts the first turn.
     *
     * @param character The {@link Character} instance to append to combat.
     */
    public void addCharacter(Character character) {
        characters.add(character);
        sortCharacters();

        if (currentCharacter == null) {
            newTurn();
        }
        initiativeOrdered = getTurnOrder();

    }

    /**
     * Refreshes the descending sorted order and updates the initiative queue.
     * <p>
     * Typically invoked following batch attribute edits (such as initiative or status changes).
     */
    public void updateCharacters() {
        sortCharacters();
        initiativeOrdered = getTurnOrder();
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
            initiativeOrdered = getTurnOrder();
            return;
        }

        currentCharacter = null;
        if (!charactersOrdered.isEmpty()) {
            currentCharacter = charactersOrdered.get(index % charactersOrdered.size());
        }
        initiativeOrdered = getTurnOrder();

    }

    /**
     * Advances combat to the next character's turn.
     * <p>
     * Rotates cyclically to the beginning of the initiative list once the end of
     * the round is reached. Marks the prior combatant inactive and the new combatant active.
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

        initiativeOrdered = getTurnOrder();
    }

    /**
     * Generates a circular rotation of the sorted initiative list positioned so that the
     * active character is at index 0.
     *
     * @return A newly allocated {@link List} containing all combatants in current turn sequence.
     */
    public List<Character> getTurnOrder() {
        sortCharacters();
        int i = charactersOrdered.indexOf(currentCharacter);
        if (i <= 0) return new ArrayList<>(charactersOrdered);

        List<Character> rotated = new ArrayList<>(charactersOrdered.subList(i, charactersOrdered.size()));
        rotated.addAll(charactersOrdered.subList(0, i));
        return rotated;
    }

    /**
     * Clears all combatants from the manager and resets active turn state to empty.
     */
    public void clearCharacters() {
        characters.clear();
        charactersOrdered.clear();
        currentCharacter = null;
        initiativeOrdered = getTurnOrder();
    }

    /**
     * Sorts the underlying character roster by descending composite initiative score.
     */
    public void sortCharacters() {
        charactersOrdered = characters.stream()
                .sorted(Comparator.comparingDouble(Character::getInitiativeValue).reversed())
                .collect(Collectors.toList());
    }

    public void setCurrentCharacterByName(String name) {
        if (name == null) return;
        for (Character c : characters) {
            if (c.getCharacterData().get("Name").toString().equals(name)) {
                currentCharacter = c;
                break;
            }
        }
        initiativeOrdered = getTurnOrder();
    }

    /**
     * Retrieves the raw, unsorted list of combatants in their original insertion order.
     *
     * @return The mutable master {@link List} of all added characters.
     */
    public List<Character> getCharacterList() {return characters;}

    /**
     * Retrieves the combat-ready list of characters rotated to the current turn order.
     *
     * @return The {@link List} of characters starting with the active combatant at index 0.
     */
    public List<Character> getInitiativeOrder() {return initiativeOrdered;}

}
