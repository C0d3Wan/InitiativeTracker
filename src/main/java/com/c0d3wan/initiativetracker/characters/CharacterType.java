package com.c0d3wan.initiativetracker.characters;

/**
 * Defines the available alignment  categories for a combat participant.
 */
public enum CharacterType {
    /** A player-controlled hero or protagonist. */
    PLAYER,
    /** A hostile adversary or monster. */
    ENEMY,
    /** A friendly or neutral companion fighting alongside players. */
    ALLY,
    /** A composite entity representing multiple homogeneous combatants. */
    HORDE,
    /** Fallback or unrecognized combatant type. */
    UNKNOWN
}