package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utility executable class used to standalone initialise or re-create the bestiary database table
 * with all necessary columns and indices.
 */
public class BestiaryTableInitialiser {

    /**
     * Main execution entry point for table initialisation.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Initializing Bestiary Database Table ===");

        File configDir = FileManager.getDirectory();
        File dbFile = new File(configDir, "initiativetracker.db");
        String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        System.out.println("[INFO] Database location: " + dbFile.getAbsolutePath());

        String createTableSql = "CREATE TABLE IF NOT EXISTS monsters (" +
                "name TEXT PRIMARY KEY COLLATE NOCASE," +
                "group_name TEXT," +
                "type TEXT," +
                "size TEXT," +
                "alignment TEXT," +
                "armor_class INTEGER," +
                "hit_points TEXT," +              // Stored as JSON: {"average": 135, "formula": "18d8 + 54"}
                "speed TEXT," +                 // Stored as JSON: {"walk": 30, "fly": 30, ...}
                "stats TEXT," +                 // Stored as JSON: {"strength": 11, "dexterity": 16, ...}
                "challenge_rating TEXT," +
                "experience_points INTEGER," +
                "saving_throws TEXT," +         // Stored as JSON map
                "skills TEXT," +                // Stored as JSON map
                "damage_resistances TEXT," +    // Stored as JSON List<String>
                "damage_vulnerabilities TEXT," +// Stored as JSON List<String>
                "damage_immunities TEXT," +     // Stored as JSON List<String>
                "condition_immunities TEXT," +  // Stored as JSON List<String>
                "senses TEXT," +                // Stored as JSON List<String>
                "languages TEXT," +             // Stored as JSON List<String>
                "traits TEXT," +                // Stored as JSON List<Feature>
                "actions TEXT," +               // Stored as JSON List<Feature>
                "legendary_actions TEXT," +     // Stored as JSON ActionGroup
                "lair_actions TEXT," +          // Stored as JSON ActionGroup
                "is_official INTEGER DEFAULT 1" + // 1 for official content, 0 for user homebrew
                ");";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSql);
            System.out.println("[SUCCESS] The 'monsters' table was created successfully with all columns and integer is_official flag!");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create table: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== Initialization Complete ===");
    }
}