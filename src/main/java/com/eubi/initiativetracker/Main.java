package com.eubi.initiativetracker;
import com.eubi.initiativetracker.characters.Character;

public class Main {
    static void main() {
        Character character = new Character("Jasoon", 20,0, (byte)1);

        System.out.println(character.getCharacterData());
    }
}
