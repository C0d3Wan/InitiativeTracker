package com.c0d3wan.initiativetracker.characters;

import java.util.*;

/**
 * Represents a composite group or swarm of individual {@link NonePlayerCharacter} entities
 * acting collectively under a single initiative slot.
 * <p>
 * The horde tracks individual constituent members, dynamically scaling its effective health
 * as members are incapacitated.
 */
public class Horde extends NonePlayerCharacter{
    private final List<NonePlayerCharacter> npcs = new ArrayList<>();

    /**
     * Constructs a horde along with its constituent individual members.
     *
     * @param name       The base name for the horde and prefix for its members.
     * @param initiative The initiative value shared by the horde.
     * @param dex        The dexterity modifier.
     * @param maxHealth  The maximum health for each constituent member.
     * @param status     The initial {@link DeathStatus}.
     * @param amount     The total number of individual NPCs to spawn in this horde.
     */
    public Horde(String name, int initiative, int dex, int maxHealth, DeathStatus status, int amount){
        super(name, initiative, dex, amount, CharacterType.HORDE, status);
        for (int i = 1; i <= amount; i++){
            NonePlayerCharacter npc = new NonePlayerCharacter(name+i,initiative, dex, maxHealth, CharacterType.ENEMY, status);
            npc.setEnemyType(name);
            npc.addHorde(this);
            npcs.add(npc);
        }
    }

    /**
     * Constructs a horde using string identifiers.
     *
     * @param name       The base name for the horde.
     * @param initiative The initiative score.
     * @param dex        The dexterity modifier.
     * @param maxHealth  The maximum health of each member.
     * @param deathID    Case-insensitive string representation of {@link DeathStatus}.
     * @param amount     The number of individual NPCs to populate.
     */
    public Horde(String name, int initiative, int dex, int maxHealth, String deathID, int amount) {
        this(name, initiative, dex, maxHealth, NonePlayerCharacter.deathFromString(deathID), amount);
    }

    /**
     * Replaces the horde's current member list with a new collection and re-establishes parent links.
     *
     * @param newList The new list of {@link NonePlayerCharacter} members.
     */
    public void editNPCs(List<NonePlayerCharacter> newList){
        npcs.clear();
        npcs.addAll(newList);
        relinkNPCs();
    }

    /**
     * Counts how many constituent NPCs have reached 0 health and updates
     * the horde's aggregate remaining health pool accordingly.
     */
    public void checkDead(){
        int dead = 0;
        for (NonePlayerCharacter npc: npcs){
            if (npc.getHealth() == 0){
                dead++;
            }
        }
        setCurrentHealth(getMaxHealth()-dead, true);
    }

    /**
     * Re-assigns this {@code Horde} instance as the parent horde reference for all contained NPCs.
     */
    public void relinkNPCs(){
        for (NonePlayerCharacter npc: npcs){
            npc.addHorde(this);
        }
    }

    /**
     * Retrieves an unmodifiable view of all constituent NPCs in this horde.
     *
     * @return An unmodifiable {@link List} of {@link NonePlayerCharacter} instances.
     */
    public List<NonePlayerCharacter> getNpcs(){
        return Collections.unmodifiableList(npcs);
    }
}
