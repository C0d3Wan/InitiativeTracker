package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;
import com.c0d3wan.initiativetracker.filemanagement.FileManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class BestiaryDatabase {
    // Made public so your JSON loader/sync manager can share this exact GSON configuration
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

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

    public static void initTable(){
        String createTableSql = "CREATE TABLE IF NOT EXISTS monsters (" +
                "name TEXT PRIMARY KEY COLLATE NOCASE," +
                "group_name TEXT," +
                "type TEXT," +
                "size TEXT," +
                "alignment TEXT," +
                "armor_class INTEGER," +
                "hit_points TEXT," +
                "speed TEXT," +
                "stats TEXT," +
                "challenge_rating TEXT," +
                "experience_points INTEGER," +
                "saving_throws TEXT," +
                "skills TEXT," +
                "damage_resistances TEXT," +
                "damage_vulnerabilities TEXT," +
                "damage_immunities TEXT," +
                "condition_immunities TEXT," +
                "senses TEXT," +
                "languages TEXT," +
                "traits TEXT," +
                "actions TEXT," +
                "reactions TEXT," +
                "legendary_actions TEXT," +
                "lair_actions TEXT," +
                "is_official INTEGER DEFAULT 1" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);

            addColumnIfNotExists(conn, "reactions");
            addColumnIfNotExists(conn, "legendary_actions");
            addColumnIfNotExists(conn, "lair_actions");

        } catch (SQLException e) {
            System.err.println("Failed to initialize bestiary database table: " + e.getMessage());
        }
    }

    private static void addColumnIfNotExists(Connection conn, String columnName) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE monsters ADD COLUMN " + columnName + " TEXT;");
        } catch (SQLException ignored) {
            // Column likely already exists, safe to ignore
        }
    }

    public static void saveUserMonster(BestiaryDTO.Monster monster) {
        saveMonster(monster, 0);
    }

    public static void saveOfficialMonster(BestiaryDTO.Monster monster) {
        saveMonster(monster, 1);
    }

    private static void saveMonster(BestiaryDTO.Monster monster, int isOfficial) {
        if (monster == null || monster.getName() == null || monster.getName().isBlank()) return;

        String sql = "INSERT INTO monsters (" +
                "name, group_name, type, size, alignment, armor_class, hit_points, speed, stats, " +
                "challenge_rating, experience_points, saving_throws, skills, " +
                "damage_resistances, damage_vulnerabilities, damage_immunities, " +
                "condition_immunities, senses, languages, traits, actions, reactions, legendary_actions, lair_actions, is_official" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(name) DO UPDATE SET " +
                "group_name=excluded.group_name, type=excluded.type, size=excluded.size, alignment=excluded.alignment, " +
                "armor_class=excluded.armor_class, hit_points=excluded.hit_points, speed=excluded.speed, stats=excluded.stats, " +
                "challenge_rating=excluded.challenge_rating, experience_points=excluded.experience_points, " +
                "saving_throws=excluded.saving_throws, skills=excluded.skills, damage_resistances=excluded.damage_resistances, " +
                "damage_vulnerabilities=excluded.damage_vulnerabilities, damage_immunities=excluded.damage_immunities, " +
                "condition_immunities=excluded.condition_immunities, senses=excluded.senses, languages=excluded.languages, " +
                "traits=excluded.traits, actions=excluded.actions, reactions=excluded.reactions, " +
                "legendary_actions=excluded.legendary_actions, lair_actions=excluded.lair_actions, is_official=excluded.is_official;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, monster.getName().trim());
            pstmt.setString(2, monster.getGroupName());
            pstmt.setString(3, monster.getType());
            pstmt.setString(4, monster.getSize());
            pstmt.setString(5, monster.getAlignment());
            pstmt.setInt(6, monster.getArmorClass());
            pstmt.setString(7, GSON.toJson(monster.getHitPoints()));
            pstmt.setString(8, GSON.toJson(monster.getSpeed()));
            pstmt.setString(9, GSON.toJson(monster.getStats()));
            pstmt.setString(10, monster.getChallengeRating());
            pstmt.setInt(11, monster.getExperiencePoints());
            pstmt.setString(12, GSON.toJson(monster.getSavingThrows()));
            pstmt.setString(13, GSON.toJson(monster.getSkills()));
            pstmt.setString(14, GSON.toJson(monster.getDamageResistances()));
            pstmt.setString(15, GSON.toJson(monster.getDamageVulnerabilities()));
            pstmt.setString(16, GSON.toJson(monster.getDamageImmunities()));
            pstmt.setString(17, GSON.toJson(monster.getConditionImmunities()));
            pstmt.setString(18, GSON.toJson(monster.getSenses()));
            pstmt.setString(19, GSON.toJson(monster.getLanguages()));
            pstmt.setString(20, GSON.toJson(monster.getTraits()));
            pstmt.setString(21, GSON.toJson(monster.getActions()));
            pstmt.setString(22, GSON.toJson(monster.getReactions()));
            pstmt.setString(23, GSON.toJson(monster.getLegendaryActions()));
            pstmt.setString(24, GSON.toJson(monster.getLairActions()));
            pstmt.setInt(25, isOfficial);

            pstmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("Failed to save monster '" + monster.getName() + "': " + e.getMessage());
        }
    }

    public static boolean deleteMonster(String name) {
        if (name == null || name.isBlank()) return false;

        String sql = "DELETE FROM monsters WHERE name = ? AND is_official = 0";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete monster '" + name + "': " + e.getMessage());
            return false;
        }
    }

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

    private static BestiaryDTO.Monster mapResultSetToMonster(ResultSet rs) throws SQLException {
        BestiaryDTO.Monster m = new BestiaryDTO.Monster();

        m.setName(rs.getString("name"));
        m.setGroup(rs.getString("group_name"));
        m.setType(rs.getString("type"));
        m.setSize(rs.getString("size"));
        m.setAlignment(rs.getString("alignment"));
        m.setArmorClass(rs.getInt("armor_class"));
        m.setChallengeRating(rs.getString("challenge_rating"));
        m.setExperiencePoints(rs.getInt("experience_points"));

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
        m.setReactions(GSON.fromJson(rs.getString("reactions"), LIST_FEATURE_TYPE));
        m.setLegendaryActions(GSON.fromJson(rs.getString("legendary_actions"), BestiaryDTO.ActionGroup.class));
        m.setLairActions(GSON.fromJson(rs.getString("lair_actions"), BestiaryDTO.ActionGroup.class));

        return m;
    }
}