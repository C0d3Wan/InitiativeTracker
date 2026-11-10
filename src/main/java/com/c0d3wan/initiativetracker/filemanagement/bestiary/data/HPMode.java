package com.c0d3wan.initiativetracker.filemanagement.bestiary.data;

public enum HPMode {
    AVERAGE("Average HP (Static)"),
    ROLL("Rolled HP (Formula)"),
    MAX("Maximum HP"),
    CUSTOM("Custom HP");

    private final String displayName;

    HPMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}