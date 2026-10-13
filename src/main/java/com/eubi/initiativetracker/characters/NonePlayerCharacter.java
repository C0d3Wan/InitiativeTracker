package com.eubi.initiativetracker.characters;

import java.util.*;

/**
 * Represents a non-player combatant entity within the initiative tracking system.
 * <p>
 * This class encapsulates state data for an individual participant, including their
 * unique display name, rolled initiative, tie-breaker dexterity modifier, alignment/type,
 * death status, and death saving throw tally. Dexterity is converted into a two-decimal
 * fractional modifier to automatically resolve ties in turn ordering.
 */
public class NonePlayerCharacter extends Character {
    private int health;
    private int maxHealth;

    /**
     * Constructs a new NPC instance
     *
     * @param name       The unique display name of the character.
     * @param initiative The base integer initiative roll.
     * @param dex        The dexterity attribute score (typically 0–30) used for tie-breakers.
     * @param maxHealth  The max health of the character.
     * @param type       The {@link CharacterType} classification of this character.
     * @param status     The initial {@link DeathStatus} of this character.
     */
    public NonePlayerCharacter(String name, int initiative, int dex, int maxHealth, CharacterType type, DeathStatus status) {
        super(name, initiative, dex, type, status);
        this.health = maxHealth;
        this.maxHealth = maxHealth;
    }

    /**
     * Constructs a new NPC instance
     *
     * @param name       The unique display name of the character.
     * @param initiative The base integer initiative roll.
     * @param dex        The dexterity attribute score (typically 0–30) used for tie-breakers.
     * @param maxHealth  The max health of the character.
     * @param typeID     The integer ID representing the {@link CharacterType} (0: PLAYER, 1: ENEMY, 2: ALLY).
     * @param deathID    The integer ID representing the {@link DeathStatus} (0: DEAD, 1: ALIVE, 2: DOWN).
     */
    public NonePlayerCharacter(String name, int initiative, int dex,  int maxHealth, String typeID, String deathID) {
        this(name, initiative, dex, maxHealth, typeFromString(typeID), deathFromString(deathID));
    }

    /**
     * Edits the health of the chaarcter
     * <p>
     * Kills them if the run out of health
     * @param amount amount to edit health (+/-)
     */
    public void editHealth(int amount){
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        if (health <= 0){
            health = 0;
            editDeathStatus(DeathStatus.DEAD);
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }
}
