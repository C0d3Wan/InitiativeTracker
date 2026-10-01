package com.eubi.initiativetracker.characters;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterManager {
    private final List<Character> characters;
    private List<Character> charactersOrdered;

    /**
     * Constructs a new, empty CharacterManager.
     */
    public CharacterManager() {
        this.characters = new ArrayList<>();
        this.charactersOrdered = new ArrayList<>();
    }

    /**
     * Adds a new character to the underlying, unsorted tracker list.
     *
     * @param character The {@link Character} object to be added.
     */
    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    /**
     * Removes a character from the unsorted tracker list.
     * <p>
     * Note: This does not automatically update the sorted list. A subsequent call
     * to {@link #sortCharacters()} is required to reflect this removal in the ordered list.
     *
     * @param character The exact {@link Character} instance to remove.
     */
    public void removeCharacter(Character character) {
        this.characters.remove(character);
    }

    /**
     * Modifies a specific attribute of a character based on its index in the original list.
     * <p>
     * <b>Warning:</b> This method relies on integer-based attribute selection:
     * <ul>
     *   <li>0 = Edit Initiative</li>
     *   <li>1 = Edit Death Status</li>
     *   <li>2 = Edit Character Type</li>
     * </ul>
     *
     * @param characterIndex The index of the character within the original, unsorted list.
     * @param change         An integer mapped to the specific attribute you wish to change.
     * @param newVal         The new integer value to apply to the selected attribute.
     */
    public void editCharacter(int characterIndex, int change, int newVal) {
        if (change == 0){
            this.characters.get(characterIndex).editInitiativeNum(newVal);
        }else if (change == 1){
            this.characters.get(characterIndex).editDeathStatus(newVal);
        }else if (change == 2){
            this.characters.get(characterIndex).editCharacterType(newVal);
        } else{
            System.err.println("Invalid change");
        }

    }

    /**
     * Completely empties the unsorted character list.
     * Note: The sorted list will remain populated until {@link #sortCharacters()} is called again.
     */
    public void clearCharacters() {
        this.characters.clear();
    }

    /**
     * Triggers a recalculation of the sorted character list.
     * <p>
     * It processes the unsorted list and generates a new Vector ordered by
     * {@link Character#getInitiativeValue()} from highest to lowest.
     */
    public void sortCharacters() {
        this.charactersOrdered = characters.stream()
                .sorted(Comparator.comparingDouble(Character::getInitiativeValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Safely retrieves a character from the original unsorted list using its index.
     *
     * @param characterIndex The position of the character to retrieve.
     * @return The requested {@link Character}, or {@code null} if the index is out of bounds.
     */
    public Character getCharacter(int characterIndex) {
        try {
            return this.characters.get(characterIndex);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Retrieves the entire original, unsorted collection of characters.
     *
     * @return A {@link Vector} of all added characters in their insertion order.
     */
    public List<Character> getCharacterVector() {return this.characters;}

    /**
     * Retrieves the sorted collection of characters.
     *
     * @return A {@link Vector} of characters ordered by initiative descending.
     */
    public List<Character> getCharactersOrdered() {return this.charactersOrdered;}

    /**
     * Outputs a formatted representation of both the original and sorted lists to the console.
     * Used primarily to verify the underlying state of the manager during development.
     */
    public void debugDisplay() {
        System.out.println("\n=== Character Manager Debug Output ===");

        System.out.println("\nOriginal List (" + this.characters.size() + " characters):");
        if (this.characters.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (int i = 0; i < this.characters.size(); i++) {
                System.out.printf("  [%d] %s%n", i, this.characters.get(i).toString());
            }
        }

        this.sortCharacters(); // Refresh the sorted list automatically for accuracy

        System.out.println("\nSorted List (By Initiative Descending):");
        if (this.charactersOrdered.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (int i = 0; i < this.charactersOrdered.size(); i++) {
                System.out.printf("  [%d] %s%n", i, this.charactersOrdered.get(i).toString());
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }
}
