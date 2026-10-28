package com.c0d3wan.initiativetracker.characters;

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
    private final int maxHealth;
    private String enemyType = "";
    private Horde horde;


    public NonePlayerCharacter(String name, int initiative, int dex, int maxHealth, CharacterType type, DeathStatus status) {
        super(name, initiative, dex, type, status);
        this.health = maxHealth;
        this.maxHealth = maxHealth;
    }

    public NonePlayerCharacter(String name, int initiative, int dex,  int maxHealth, String typeID, String deathID) {
        this(name, initiative, dex, maxHealth, typeFromString(typeID), deathFromString(deathID));
    }

    public void editHealth(int amount){
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        if (health <= 0){
            health = 0;
            setDeathStatus(DeathStatus.DEAD);
            horde.checkDead();
        } else {
            setDeathStatus(DeathStatus.ALIVE);
        }
    }

    public void setEnemyType(String type){
        enemyType = type;
    }

    public void setCurrentHealth(int currentHealth, boolean checkDeath) {
        this.health = Math.clamp(currentHealth, 0, maxHealth);
        if (this.health == 0) {
            setDeathStatus(DeathStatus.DEAD);
            if (checkDeath && horde != null) {
                horde.checkDead();
            }
        } else {
            setDeathStatus(DeathStatus.ALIVE);
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public String getEnemyType() {
        return enemyType;
    }

    public void addHorde(Horde hordeClass) {
        horde = hordeClass;
    }
}
