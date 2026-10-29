package com.c0d3wan.initiativetracker.characters;

public record CharacterData(
        String name,
        int initiative,
        int dexterity,
        float totalInitiative,
        CharacterType type,
        DeathStatus deathStatus
) {}
