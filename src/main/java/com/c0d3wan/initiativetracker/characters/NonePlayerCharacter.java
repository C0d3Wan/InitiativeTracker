package com.c0d3wan.initiativetracker.characters;

/**
 * Represents a non-player combatant entity within the initiative tracking system.
 * <p>
 * Encapsulates hit point tracking, subclassification tags (such as enemy archetype),
 * and dynamic synchronization with an owning parent {@link Horde} if attached.
 */
public class NonePlayerCharacter extends Character {
    private int health;
    private final int maxHealth;
    private String enemyType = "";
    private Horde horde;

    /**
     * Constructs a new {@code NonePlayerCharacter}.
     *
     * @param name       The display name of the NPC.
     * @param initiative The rolled base initiative value.
     * @param dex        The dexterity modifier/score.
     * @param maxHealth  The maximum health pool (initial health will equal this value).
     * @param type       The {@link CharacterType} classification.
     * @param status     The initial {@link DeathStatus}.
     */
    public NonePlayerCharacter(String name, int initiative, int dex, int maxHealth, CharacterType type, DeathStatus status) {
        super(name, initiative, dex, type, status);
        this.health = maxHealth;
        this.maxHealth = maxHealth;
    }

    /**
     * Constructs a new {@code NonePlayerCharacter} using string identifiers.
     *
     * @param name       The display name of the NPC.
     * @param initiative The rolled base initiative value.
     * @param dex        The dexterity modifier/score.
     * @param maxHealth  The maximum health pool.
     * @param typeID     Case-insensitive string representation of {@link CharacterType}.
     * @param deathID    Case-insensitive string representation of {@link DeathStatus}.
     */
    public NonePlayerCharacter(String name, int initiative, int dex,  int maxHealth, String typeID, String deathID) {
        this(name, initiative, dex, maxHealth, typeFromString(typeID), deathFromString(deathID));
    }

    /**
     * Modifies current health by a relative delta (healing or damage).
     * <p>
     * Constrains health between 0 and {@code maxHealth}. If reduced to 0, sets status
     * to {@link DeathStatus#DEAD} and notifies any associated parent {@link Horde}.
     *
     * @param amount The delta to apply (positive for healing, negative for damage).
     */
    public void editHealth(int amount){
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
        if (health <= 0){
            health = 0;
            setDeathStatus(DeathStatus.DEAD);
            if (horde != null) {
                horde.checkDead();
            }
        } else {
            setDeathStatus(DeathStatus.ALIVE);
        }
    }

    /**
     * Sets the subtype descriptor for this enemy (e.g., "Goblin Archer").
     *
     * @param type The descriptive enemy subtype name.
     */
    public void setEnemyType(String type){
        enemyType = type;
    }

    /**
     * Directly sets the current health value clamped within [0, {@code maxHealth}].
     *
     * @param currentHealth The target hit points.
     * @param checkDeath    If {@code true} and health drops to 0, triggers a dead count
     *                      verification on the parent {@link Horde}.
     */
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

    /**
     * Gets the maximum hit point threshold.
     *
     * @return The maximum health integer.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets current hit points.
     *
     * @return The current health integer.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the descriptive enemy subtype.
     *
     * @return The subtype string.
     */
    public String getEnemyType() {
        return enemyType;
    }

    /**
     * Associates this NPC with an overarching parent swarm or horde.
     *
     * @param hordeClass The parent {@link Horde} instance.
     */
    public void addHorde(Horde hordeClass) {
        horde = hordeClass;
    }
}
