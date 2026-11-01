package com.c0d3wan.initiativetracker.characters;

/**
 * Defines the health and mortality states of a combatant.
 */
public enum DeathStatus {
    /** The character has expired and is removed from active combat turns. */
    DEAD,
    /** The character is conscious and able to act. */
    ALIVE,
    /** The character is incapacitated at 0 hit points and undergoing death saving throws. */
    DOWN,
    /** Fallback or uninitialized health state. */
    UNKNOWN
}
