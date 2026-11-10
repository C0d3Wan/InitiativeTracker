package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.c0d3wan.initiativetracker.filemanagement.bestiary.data.BestiaryDTO;

import java.io.File;
import java.sql.*;
import java.util.*;

public class BestiaryDatabase {

    private static String getDbUrl() {
        File dbFile = new File(FileManager.getDirectory(), "bestiary.db");
        return "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(getDbUrl());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static List<BestiaryDTO.Monster> loadAllMonsters() {
        List<BestiaryDTO.Monster> monsters = new ArrayList<>();
        String query = "SELECT m.*, g.name AS group_name, t.name AS type_name, s.name AS size_name, a.name AS alignment_id_name " +
                "FROM Monster m " +
                "LEFT JOIN MonsterGroup g ON m.group_id = g.id " +
                "LEFT JOIN MonsterType t ON m.type_id = t.id " +
                "LEFT JOIN Size s ON m.size_id = s.id " +
                "LEFT JOIN Alignment a ON m.alignment_id = a.id";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                BestiaryDTO.Monster monster = new BestiaryDTO.Monster();
                long monsterId = rs.getLong("id");

                monster.setName(rs.getString("name"));
                monster.setGroupName(rs.getString("group_name"));
                monster.setType(rs.getString("type_name"));
                monster.setSize(rs.getString("size_name"));
                monster.setAlignment(rs.getString("alignment_id_name"));
                monster.setArmorClass(rs.getInt("armor_class"));
                monster.setChallengeRating(String.valueOf(rs.getDouble("challenge_rating")));
                monster.setExperiencePoints(rs.getInt("experience_points"));
                monster.setOfficial(rs.getInt("is_official") == 1);

                BestiaryDTO.Monster.HitPoints hp = new BestiaryDTO.Monster.HitPoints();
                hp.setAverage(rs.getInt("hp_average"));
                hp.setFormula(rs.getString("hp_formula"));
                monster.setHitPoints(hp);

                monster.setStats(loadStats(conn, monsterId));
                monster.setSpeed(loadSpeeds(conn, monsterId));
                monster.setSenses(loadSenses(conn, monsterId));
                monster.setLanguages(loadLanguages(conn, monsterId));
                monster.setTags(loadTags(conn, monsterId)); // <-- Populates tags so tree sub-headers appear!
                monster.setTraits(loadAbilities(conn, monsterId, "Trait"));
                monster.setActions(loadAbilities(conn, monsterId, "Action"));
                monster.setReactions(loadAbilities(conn, monsterId, "Reaction"));

                monsters.add(monster);
            }
        } catch (SQLException e) {
            System.err.println("Error loading monsters from database: " + e.getMessage());
        }
        return monsters;
    }

    public static Optional<BestiaryDTO.Monster> findMonsterByName(String name) {
        return loadAllMonsters().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static void saveUserMonster(BestiaryDTO.Monster monster) {
        String insertSql = "INSERT OR REPLACE INTO Monster (name, group_id, type_id, size_id, alignment_id, challenge_rating, armor_class, hp_average, hp_formula, experience_points, is_official) " +
                "VALUES (?, (SELECT id FROM MonsterGroup WHERE name = ?), (SELECT id FROM MonsterType WHERE name = ?), (SELECT id FROM Size WHERE name = ?), (SELECT id FROM Alignment WHERE name = ?), ?, ?, ?, ?, ?, 0)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, monster.getName());
            pstmt.setString(2, monster.getGroupName() != null ? monster.getGroupName() : "Homebrew");
            pstmt.setString(3, monster.getType());
            pstmt.setString(4, monster.getSize());
            pstmt.setString(5, monster.getAlignment());
            pstmt.setDouble(6, Double.parseDouble(monster.getChallengeRating()));
            pstmt.setInt(7, monster.getArmorClass());
            pstmt.setInt(8, monster.getHitPoints() != null ? monster.getHitPoints().getAverage() : 10);
            pstmt.setString(9, monster.getHitPoints() != null ? monster.getHitPoints().getFormula() : "2d8");
            pstmt.setInt(10, monster.getExperiencePoints());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user homebrew monster: " + e.getMessage());
        }
    }

    public static boolean deleteMonster(String name) {
        String query = "DELETE FROM Monster WHERE name = ? AND is_official = 0";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting monster: " + e.getMessage());
            return false;
        }
    }

    private static Map<String, Integer> loadStats(Connection conn, long monsterId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT strength, dexterity, constitution, intelligence, wisdom, charisma FROM MonsterStats WHERE monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("strength", rs.getInt("strength"));
                    stats.put("dexterity", rs.getInt("dexterity"));
                    stats.put("constitution", rs.getInt("constitution"));
                    stats.put("intelligence", rs.getInt("intelligence"));
                    stats.put("wisdom", rs.getInt("wisdom"));
                    stats.put("charisma", rs.getInt("charisma"));
                }
            }
        }
        return stats;
    }

    private static Map<String, Integer> loadSpeeds(Connection conn, long monsterId) throws SQLException {
        Map<String, Integer> speeds = new HashMap<>();
        String query = "SELECT st.name AS type, ms.speed_ft FROM MonsterSpeed ms JOIN SpeedType st ON ms.speed_type_id = st.id WHERE ms.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    speeds.put(rs.getString("type").toLowerCase(), rs.getInt("speed_ft"));
                }
            }
        }
        return speeds;
    }

    private static List<String> loadSenses(Connection conn, long monsterId) throws SQLException {
        List<String> senses = new ArrayList<>();
        String query = "SELECT st.name AS type, ms.range_ft FROM MonsterSense ms JOIN SenseType st ON ms.sense_type_id = st.id WHERE ms.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    senses.add(rs.getString("type") + " " + rs.getInt("range_ft") + " ft.");
                }
            }
        }
        return senses;
    }

    private static List<String> loadLanguages(Connection conn, long monsterId) throws SQLException {
        List<String> langs = new ArrayList<>();
        String query = "SELECT l.name FROM MonsterLanguage ml JOIN Language l ON ml.language_id = l.id WHERE ml.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    langs.add(rs.getString("name"));
                }
            }
        }
        return langs;
    }

    private static List<String> loadTags(Connection conn, long monsterId) throws SQLException {
        List<String> tags = new ArrayList<>();
        String query = "SELECT t.name FROM MonsterTag mt JOIN Tag t ON mt.tag_id = t.id WHERE mt.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(rs.getString("name"));
                }
            }
        }
        return tags;
    }

    private static List<BestiaryDTO.Monster.Feature> loadAbilities(Connection conn, long monsterId, String abilityType) throws SQLException {
        List<BestiaryDTO.Monster.Feature> features = new ArrayList<>();
        String query = "SELECT name, description FROM MonsterAbility WHERE monster_id = ? AND ability_type = ? ORDER BY display_order";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            pstmt.setString(2, abilityType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BestiaryDTO.Monster.Feature f = new BestiaryDTO.Monster.Feature();
                    f.setName(rs.getString("name"));
                    f.setDescription(rs.getString("description"));
                    features.add(f);
                }
            }
        }
        return features;
    }
}