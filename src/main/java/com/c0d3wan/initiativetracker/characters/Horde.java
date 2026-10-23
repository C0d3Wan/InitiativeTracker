package com.c0d3wan.initiativetracker.characters;

import com.c0d3wan.initiativetracker.characters.NonePlayerCharacter;

import java.util.*;

public class Horde extends Character{
    private final List<NonePlayerCharacter> NPCs = new ArrayList<>();

    public Horde(String name, int initiative, int dex, int maxHealth, CharacterType type, DeathStatus status, int amount){
        super(name, initiative, dex,type, status);
        for (int i = 1; i <= amount; i++){
            NPCs.add(new NonePlayerCharacter(name+i,initiative, dex, maxHealth, type, status));
        }
    }

    public Horde(String name, int initiative, int dex,  int maxHealth, String typeID, String deathID, int amount) {
        this(name, initiative, dex, maxHealth, NonePlayerCharacter.typeFromString(typeID), NonePlayerCharacter.deathFromString(deathID), amount);
    }

    public List<NonePlayerCharacter> getNPCs(){
        return NPCs;
    }
}
