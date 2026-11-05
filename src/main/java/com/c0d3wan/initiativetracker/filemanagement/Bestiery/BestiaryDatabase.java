package com.c0d3wan.initiativetracker.filemanagement.Bestiery;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.google.gson.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class BestiaryDatabase {
    private static final Gson GSON= new GsonBuilder().create();
    private static final String DB_NAME = "initiativetracker.db";
    private static final String DB_URL;

    private static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
    private static final Type MAP_STRING_INT_TYPE = new TypeToken<>() {}.getType();

    static {
        File configDir = FileManager.getDirectory();
        File dbFile = new File(configDir, DB_NAME);

        DB_URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        initTable();
    }

    /**
     * Initialises the SQLite monsters table with all individual attribute columns.
     */
    public static void initTable(){
        String createTableSql = "CREATE TABLE IF NOT EXISTS monsters (" +
                "name TEXT PRIMARY KEY COLLATE NOCASE," +
                "type TEXT," +
                "size TEXT," +
                "alignment TEXT," +
                "armor_class INTEGER," +
                "hit_points TEXT," +              // JSON object
                "speed TEXT," +                 // JSON map
                "stats TEXT," +                 // JSON map
                "challenge_rating TEXT," +
                "experience_points INTEGER," +
                "saving_throws TEXT," +         // JSON map
                "skills TEXT," +                // JSON map
                "damage_resistances TEXT," +    // JSON list
                "damage_vulnerabilities TEXT," +// JSON list
                "damage_immunities TEXT," +     // JSON list
                "condition_immunities TEXT," +  // JSON list
                "senses TEXT," +                // JSON list
                "languages TEXT," +             // JSON list
                "traits TEXT," +                // JSON list
                "actions TEXT," +               // JSON list
                "legendary_actions TEXT," +     // JSON object/group
                "lair_actions TEXT," +          // JSON object/group
                "is_official INTEGER DEFAULT 1" + // 1 for official, 0 for homebrew
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            System.err.println("Failed to initialize bestiary database table: " + e.getMessage());
        }
    }


}
