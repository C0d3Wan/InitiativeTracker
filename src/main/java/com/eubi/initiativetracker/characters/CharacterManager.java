package com.eubi.initiativetracker.characters;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterManager {
    private final List<Character> characters;
    private List<Character> charactersOrdered;
    private Character currentCharacter;

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
        this.sortCharacters();
    }

    /**
     * Removes a character from the unsorted tracker list.
     *
     * @param character The exact {@link Character} instance to remove.
     */
    public void removeCharacter(Character character) {
        this.characters.remove(character);
        this.sortCharacters(); // Realign the turn order

        if (this.currentCharacter != null && this.currentCharacter.equals(character)) {
            this.newTurn();
        }
    }

    /**
     * Advances the turn tracker to the next character in ordered list.
     * Operates cyclically, returning to the top of the list when reaching the end.
     */
    public void newTurn(){
        if(this.charactersOrdered.isEmpty()){
            this.currentCharacter = null;
            return;
        }

        if (this.currentCharacter == null){
            this.currentCharacter = this.charactersOrdered.get(0);
            return;
        }

        int currentIndex = this.charactersOrdered.indexOf(this.currentCharacter);

        if (currentIndex == -1){
            this.currentCharacter = this.charactersOrdered.get(0);
        }else  {
            int nextIndex = (currentIndex + 1) % this.charactersOrdered.size();
            this.currentCharacter = this.charactersOrdered.get(nextIndex);
        }
    }

    /**
     * Updates the initiative score of a specific character.
     *
     * @param characterIndex The index of the character in the unsorted list.
     * @param newInitiative  The new base initiative score.
     *
     */
    public void editCharacterInitiative(int characterIndex, float newInitiative) {
        this.characters.get(characterIndex).editInitiativeNum(newInitiative);
    }

    /**
     * Updates the death status of a specific character.
     *
     * @param characterIndex The index of the character in the unsorted list.
     * @param newStatus      The new {@link DeathStatus} enum value.
     */
    public void editCharacterStatus(int characterIndex, DeathStatus newStatus) {
        this.characters.get(characterIndex).editDeathStatus(newStatus);
    }

    /**
     * Updates the specific character.
     *
     * @param characterIndex The index of the character in the unsorted list.
     * @param newType        The new {@link CharacterType} enum value.
     */
    public void editCharacterType(int characterIndex, CharacterType newType) {
        this.characters.get(characterIndex).editCharacterType(newType);
    }

    /**
     * Completely empties the unsorted character list.
     * Note: The sorted list will remain populated until {@link #sortCharacters()} is called again.
     */
    public void clearCharacters() {
        this.characters.clear();
        this.charactersOrdered.clear();
        this.currentCharacter = null;
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
     * Retrieves the character whose turn it currently is.
     *
     * @return The active {@link Character}.
     */
    public Character getCurrentCharacter() {
        return this.currentCharacter;
    }

    /**
     * Retrieves the entire original, unsorted collection of characters.
     *
     * @return A {@link List} of all added characters in their insertion order.
     */
    public List<Character> getCharacterVector() {return this.characters;}

    /**
     * Retrieves the sorted collection of characters.
     *
     * @return A {@link List} of characters ordered by initiative descending.
     */
    public List<Character> getCharactersOrdered() {return this.charactersOrdered;}

    /**
     * Outputs a formatted representation of both the original and sorted lists to the console.
     * Used primarily to verify the underlying state of the manager during development.
     */
    public void debugDisplay(boolean sort) {
        System.out.println("\n=== Character Manager Debug Output ===");
        if (this.currentCharacter != null) {
            System.out.println("Current Turn: " + this.currentCharacter.toString());
        }else{
            System.out.println("Current Turn: " + "N/A");
        }

        System.out.println("\nOriginal List (" + this.characters.size() + " characters):");
        if (this.characters.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (int i = 0; i < this.characters.size(); i++) {
                System.out.printf("  [%d] %s%n", i, this.characters.get(i).toString());
            }
        }
        System.out.println("\nSorted List (By Initiative Descending):");
        if (this.characters.isEmpty() || !sort) {
            System.out.println(" ");
        } else {
            this.sortCharacters();
        }
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
