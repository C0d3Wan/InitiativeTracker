package com.c0d3wan.initiativetracker.characters;

import java.util.*;

public class Horde extends NonePlayerCharacter{
    private final List<NonePlayerCharacter> NPCs = new ArrayList<>();

    public Horde(String name, int initiative, int dex, int maxHealth, DeathStatus status, int amount){
        super(name, initiative, dex, amount, CharacterType.HORDE, status);
        for (int i = 1; i <= amount; i++){
            NonePlayerCharacter npc = new NonePlayerCharacter(name+i,initiative, dex, maxHealth, CharacterType.ENEMY, status);
            npc.editEnemyType(name);
            NPCs.add(npc);
        }
    }

    public Horde(String name, int initiative, int dex, int maxHealth, String deathID, int amount) {
        this(name, initiative, dex, maxHealth, NonePlayerCharacter.deathFromString(deathID), amount);
    }

    public void checkDead(){
        for (NonePlayerCharacter npc: NPCs){
            if (npc.getHealth() == 0){
                editHealth(-1);
            }
        }
    }

    public List<NonePlayerCharacter> getNPCs(){
        return NPCs;
    }
}
