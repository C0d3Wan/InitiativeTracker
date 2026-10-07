package com.eubi.initiativetracker.characters;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterManager {
    private final List<Character> characters;
    private List<Character> charactersOrdered;
    private List<Character> initiativeOrdered;
    private Character currentCharacter;

    /**
     * Constructs a new, empty CharacterManager.
     */
    public CharacterManager() {
        characters = new ArrayList<>();
        charactersOrdered = new ArrayList<>();
        initiativeOrdered = new ArrayList<>();
    }

    /**
     * Adds a new character to the underlying, unsorted tracker list.
     *
     * @param character The {@link Character} object to be added.
     */
    public void addCharacter(Character character) {
        characters.add(character);
        sortCharacters();

        if (currentCharacter == null) {
            newTurn();
        }
        initiativeOrdered = getTurnOrder();

    }

    public void updateCharacters() {
        sortCharacters();
        initiativeOrdered = getTurnOrder();
    }

    /**
     * Removes a character from the unsorted tracker list.
     *
     * @param character The exact {@link Character} instance to remove.
     */
    public void removeCharacter(Character character) {
        sortCharacters();
        boolean wasCurrent = (character == currentCharacter);
        int index = charactersOrdered.indexOf(character);

        characters.remove(character);
        character.editCurrentCharacter(false);
        sortCharacters();


        if (!wasCurrent) {
            initiativeOrdered = getTurnOrder();
            return;
        }

        currentCharacter = null;
        if (!charactersOrdered.isEmpty()) {
            currentCharacter = charactersOrdered.get(index % charactersOrdered.size());
            currentCharacter.editCurrentCharacter(true);
        }
        initiativeOrdered = getTurnOrder();

    }

    /**
     * Advances the turn tracker to the next character in ordered list.
     * Operates cyclically, returning to the top of the list when reaching the end.
     */
    public void newTurn() {
        sortCharacters();
        if (charactersOrdered.isEmpty()) {
            currentCharacter = null;
            return;
        }

        int nextIndex = 0;
        if (currentCharacter != null) {
            currentCharacter.editCurrentCharacter(false);
            int currentIndex = charactersOrdered.indexOf(currentCharacter);
            nextIndex = (currentIndex + 1) % charactersOrdered.size();
        }

        currentCharacter = charactersOrdered.get(nextIndex);
        currentCharacter.editCurrentCharacter(true);

        initiativeOrdered = getTurnOrder();
    }

    public List<Character> getTurnOrder() {
        sortCharacters();
        int i = charactersOrdered.indexOf(currentCharacter);
        if (i <= 0) return new ArrayList<>(charactersOrdered);

        List<Character> rotated = new ArrayList<>(charactersOrdered.subList(i, charactersOrdered.size()));
        rotated.addAll(charactersOrdered.subList(0, i));
        return rotated;
    }



    /**
     * Completely empties the unsorted character list.
     * Note: The sorted list will remain populated until {@link #sortCharacters()} is called again.
     */
    public void clearCharacters() {
        characters.clear();
        charactersOrdered.clear();
        currentCharacter = null;
        initiativeOrdered = getTurnOrder();
    }

    /**
     * Triggers a recalculation of the sorted character list.
     * <p>
     * It processes the unsorted list and generates a new Vector ordered by
     * {@link Character#getInitiativeValue()} from highest to lowest.
     */
    public void sortCharacters() {
        charactersOrdered = characters.stream()
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
            return characters.get(characterIndex);
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
        return currentCharacter;
    }

    /**
     * Retrieves the entire original, unsorted collection of characters.
     *
     * @return A {@link List} of all added characters in their insertion order.
     */
    public List<Character> getCharacterList() {return characters;}

    /**
     * Retrieves the sorted collection of characters.
     *
     * @return A {@link List} of characters ordered by initiative descending.
     */
    public List<Character> getInitiativeOrder() {return initiativeOrdered;}

    /**
     * Outputs a formatted representation of both the original and sorted lists to the console.
     * Used primarily to verify the underlying state of the manager during development.
     */
    public void debugDisplay(boolean sort) {
        System.out.println("\n=== Character Manager Debug Output ===");
        if (currentCharacter != null) {
            System.out.println("Current Turn: " + currentCharacter.toString());
        }else{
            System.out.println("Current Turn: " + "N/A");
        }

        System.out.println("\nOriginal List (" + characters.size() + " characters):");
        if (characters.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (int i = 0; i < characters.size(); i++) {
                System.out.printf("  [%d] %s%n", i, characters.get(i).toString());
            }
        }
        System.out.println("\nSorted List (By Initiative Descending):");
        if (characters.isEmpty() || !sort) {
            System.out.println(" ");
        } else {
            sortCharacters();
        }
        if (charactersOrdered.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (int i = 0; i < charactersOrdered.size(); i++) {
                System.out.printf("  [%d] %s%n", i, charactersOrdered.get(i).toString());
            }
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }
}
