package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.filemanagement.FileManager;

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
    private static final Type LIST_FEATURE_TYPE = new TypeToken<List<BestiaryDTO.Feature>>() {}.getType();

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

    /**
     * Saves or updates a user-created homebrew monster (is_official = 0).
     */
    public static void saveUserMonster(BestiaryDTO.Monster monster) {
        saveMonster(monster, 0);
    }

    /**
     * Saves or updates an official monster (is_official = 1).
     */
    public static void saveOfficialMonster(BestiaryDTO.Monster monster) {
        saveMonster(monster, 1);
    }

    /**
     * Internal helper to persist a monster into SQLite across all mapped columns.
     */
    private static void saveMonster(BestiaryDTO.Monster monster, int isOfficial) {
        if (monster == null || monster.getName() == null || monster.getName().isBlank()) return;

        String sql = "INSERT OR REPLACE INTO monsters (" +
                "name, type, size, alignment, armor_class, hit_points, speed, stats, " +
                "challenge_rating, experience_points, saving_throws, skills, " +
                "damage_resistances, damage_vulnerabilities, damage_immunities, " +
                "condition_immunities, senses, languages, traits, actions, legendary_actions, lair_actions, is_official" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, monster.getName().trim());
            pstmt.setString(2, monster.getType());
            pstmt.setString(3, monster.getSize());
            pstmt.setString(4, monster.getAlignment());
            pstmt.setInt(5, monster.getArmorClass());
            pstmt.setString(6, GSON.toJson(monster.getHitPoints()));
            pstmt.setString(7, GSON.toJson(monster.getSpeed()));
            pstmt.setString(8, GSON.toJson(monster.getStats()));
            pstmt.setString(9, monster.getChallengeRating());
            pstmt.setInt(10, monster.getExperiencePoints());
            pstmt.setString(11, GSON.toJson(monster.getSavingThrows()));
            pstmt.setString(12, GSON.toJson(monster.getSkills()));
            pstmt.setString(13, GSON.toJson(monster.getDamageResistances()));
            pstmt.setString(14, GSON.toJson(monster.getDamageVulnerabilities()));
            pstmt.setString(15, GSON.toJson(monster.getDamageImmunities()));
            pstmt.setString(16, GSON.toJson(monster.getConditionImmunities()));
            pstmt.setString(17, GSON.toJson(monster.getSenses()));
            pstmt.setString(18, GSON.toJson(monster.getLanguages()));
            pstmt.setString(19, GSON.toJson(monster.getTraits()));
            pstmt.setString(20, GSON.toJson(monster.getActions()));
            pstmt.setString(21, GSON.toJson(monster.getLegendaryActions()));
            pstmt.setString(22, GSON.toJson(monster.getLairActions()));
            pstmt.setInt(23, isOfficial);

            pstmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("Failed to save monster '" + monster.getName() + "': " + e.getMessage());
        }
    }

    /**
     * Loads all monsters from the database and fully reconstructs their DTO objects.
     */
    public static List<BestiaryDTO.Monster> loadAllMonsters() {
        List<BestiaryDTO.Monster> monsters = new ArrayList<>();
        String sql = "SELECT * FROM monsters";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                monsters.add(mapResultSetToMonster(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load monsters from database: " + e.getMessage());
        }
        return monsters;
    }
    /**
     * Finds a specific monster by name.
     */
    public static Optional<BestiaryDTO.Monster> findMonsterByName(String name) {
        if (name == null || name.isBlank()) return Optional.empty();

        String sql = "SELECT * FROM monsters WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMonster(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to query monster by name: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Helper method to map a database ResultSet row back into a fully hydrated Monster DTO.
     */
    private static BestiaryDTO.Monster mapResultSetToMonster(ResultSet rs) throws SQLException {
        BestiaryDTO.Monster m = new BestiaryDTO.Monster();

        m.setName(rs.getString("name"));
        m.setType(rs.getString("type"));
        m.setSize(rs.getString("size"));
        m.setAlignment(rs.getString("alignment"));
        m.setArmorClass(rs.getInt("armor_class"));
        m.setChallengeRating(rs.getString("challenge_rating"));
        m.setExperiencePoints(rs.getInt("experience_points"));

        // Deserialize JSON columns back into complex objects/lists using Gson
        m.setHitPoints(GSON.fromJson(rs.getString("hit_points"), BestiaryDTO.HitPoints.class));
        m.setSpeed(GSON.fromJson(rs.getString("speed"), MAP_STRING_INT_TYPE));
        m.setStats(GSON.fromJson(rs.getString("stats"), MAP_STRING_INT_TYPE));
        m.setSavingThrows(GSON.fromJson(rs.getString("saving_throws"), MAP_STRING_INT_TYPE));
        m.setSkills(GSON.fromJson(rs.getString("skills"), MAP_STRING_INT_TYPE));

        m.setDamageResistances(GSON.fromJson(rs.getString("damage_resistances"), LIST_STRING_TYPE));
        m.setDamageVulnerabilities(GSON.fromJson(rs.getString("damage_vulnerabilities"), LIST_STRING_TYPE));
        m.setDamageImmunities(GSON.fromJson(rs.getString("damage_immunities"), LIST_STRING_TYPE));
        m.setConditionImmunities(GSON.fromJson(rs.getString("condition_immunities"), LIST_STRING_TYPE));
        m.setSenses(GSON.fromJson(rs.getString("senses"), LIST_STRING_TYPE));
        m.setLanguages(GSON.fromJson(rs.getString("languages"), LIST_STRING_TYPE));

        m.setTraits(GSON.fromJson(rs.getString("traits"), LIST_FEATURE_TYPE));
        m.setActions(GSON.fromJson(rs.getString("actions"), LIST_FEATURE_TYPE));
        m.setLegendaryActions(GSON.fromJson(rs.getString("legendary_actions"), BestiaryDTO.ActionGroup.class));
        m.setLairActions(GSON.fromJson(rs.getString("lair_actions"), BestiaryDTO.ActionGroup.class));

        return m;
    }

}
