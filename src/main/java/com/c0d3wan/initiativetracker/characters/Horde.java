package com.c0d3wan.initiativetracker.characters;

import java.util.*;

public class Horde extends NonePlayerCharacter{
    private final List<NonePlayerCharacter> npcs = new ArrayList<>();

    public Horde(String name, int initiative, int dex, int maxHealth, DeathStatus status, int amount){
        super(name, initiative, dex, amount, CharacterType.HORDE, status);
        for (int i = 1; i <= amount; i++){
            NonePlayerCharacter npc = new NonePlayerCharacter(name+i,initiative, dex, maxHealth, CharacterType.ENEMY, status);
            npc.setEnemyType(name);
            npc.addHorde(this);
            npcs.add(npc);
        }
    }

    public Horde(String name, int initiative, int dex, int maxHealth, String deathID, int amount) {
        this(name, initiative, dex, maxHealth, NonePlayerCharacter.deathFromString(deathID), amount);
    }

    public void editNPCs(List<NonePlayerCharacter> newList){
        npcs.clear();
        npcs.addAll(newList);
        relinkNPCs();
    }
    public void checkDead(){
        int dead = 0;
        for (NonePlayerCharacter npc: npcs){
            if (npc.getHealth() == 0){
                dead++;
            }
        }
        setCurrentHealth(getMaxHealth()-dead, true);
    }

    public void relinkNPCs(){
        for (NonePlayerCharacter npc: npcs){
            npc.addHorde(this);
        }
    }

    public List<NonePlayerCharacter> getNpcs(){
        return Collections.unmodifiableList(npcs);
    }
}
