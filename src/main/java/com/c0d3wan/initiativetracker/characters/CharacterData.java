package com.c0d3wan.initiativetracker.characters;

/**
 * An immutable data transfer record representing a snapshot of a character's state.
 *
 * @param name            The display name of the character.
 * @param initiative      The rolled base initiative value.
 * @param dexterity       The dexterity score used for tie-breakers.
 * @param totalInitiative The calculated final initiative score including dexterity tie-breaking fractions.
 * @param type            The character's alignment {@link CharacterType}.
 * @param deathStatus     The current life/death status {@link DeathStatus}.
 */
public record CharacterData(
        String name,
        int initiative,
        int dexterity,
        float totalInitiative,
        CharacterType type,
        DeathStatus deathStatus
) {}
