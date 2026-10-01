package com.eubi.initiativetracker.characters;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterManager {
    private final Vector<Character> characters;
    private Vector<Character> charactersOrdered;

    public CharacterManager() {
        this.characters = new Vector<>();
        this.charactersOrdered = new Vector<>();
    }

    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    public void removeCharacter(Character character) {
        this.characters.remove(character);
    }

    public void editCharacter(int characterIndex, int change, int newVal) {
        //allows changing of character
        //0 for initiative 1 for death status
        if (change == 0){
            this.characters.elementAt(characterIndex).editInitiativeNum(newVal);
        }else if (change == 1){
            this.characters.elementAt(characterIndex).editDeathStatus(newVal);
        }else if (change == 2){
            this.characters.elementAt(characterIndex).editCharacterType(newVal);
        } else{
            System.err.println("Invalid change");
        }

    }

    public void clearCharacters() {
        this.characters.clear();
    }

    public void sortCharacters() {
        this.charactersOrdered = characters.stream()
                .sorted(Comparator.comparingDouble(Character::getInitiativeValue))
                .collect(Collectors.toCollection(Vector::new));
    }

    public Character getCharacter(int characterIndex) {
        try {
            return this.characters.elementAt(characterIndex);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Vector<Character> getCharacterVector() {return this.characters;}

    public Vector<Character> getCharactersOrdered() {return this.charactersOrdered;}

    //debug commands


    public void debugDisplay(){
        System.out.println("Character Manager");
        Vector<String> temp =new Vector<>();
        for (Character character : this.characters) {
            temp.add(character.toString());
        }
        System.out.println("Original List: ");
        System.out.println(temp);
        temp =new Vector<>();
        this.sortCharacters();
        for ( Character character : this.charactersOrdered) {
            temp.add(character.toString());
        }
        System.out.println("Sorted List: ");
        System.out.println(temp);
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }
}
