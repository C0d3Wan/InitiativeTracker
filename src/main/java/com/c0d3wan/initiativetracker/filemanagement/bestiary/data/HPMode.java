package com.c0d3wan.initiativetracker.filemanagement.bestiary.data;

/**
 * Enumeration representing the health calculation mode when adding a monster to the initiative tracker.
 */
public enum HPMode {
    /** Uses the official average hit point value. */
    AVERAGE,
    /** Rolls hit points dynamically using the monster's hit dice formula. */
    ROLL,
    /** Uses a manually specified custom health value. */
    MANUAL
}