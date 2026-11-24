package com.c0d3wan.initiativetracker.filemanagement.bestiary;

import com.c0d3wan.initiativetracker.filemanagement.FileManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class DatabaseToJsonExporter {

    private static String getDatabaseUrl() {
        File dbFile = new File(FileManager.getDirectory(), "bestiary.db");
        return "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public static void main(String[] args) {
        System.out.println("Connecting to config database and exporting to resources JSON...");

        JsonObject rootJson = new JsonObject();
        JsonArray monstersArray = new JsonArray();

        try (Connection conn = DriverManager.getConnection(getDatabaseUrl())) {
            String monsterQuery = "SELECT m.id, m.name, g.name AS group_name, t.name AS type_name, " +
                    "s.name AS size_name, a.name AS alignment_name, m.challenge_rating, " +
                    "m.armor_class, m.hp_average, m.hp_formula, m.experience_points, " +
                    "m.passive_perception, m.proficiency_bonus, m.description, m.is_official " +
                    "FROM Monster m " +
                    "LEFT JOIN MonsterGroup g ON m.group_id = g.id " +
                    "LEFT JOIN MonsterType t ON m.type_id = t.id " +
                    "LEFT JOIN Size s ON m.size_id = s.id " +
                    "LEFT JOIN Alignment a ON m.alignment_id = a.id";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(monsterQuery)) {

                while (rs.next()) {
                    long monsterId = rs.getLong("id");
                    JsonObject monsterObj = new JsonObject();

                    monsterObj.addProperty("name", rs.getString("name"));
                    monsterObj.addProperty("group", rs.getString("group_name"));
                    monsterObj.addProperty("type", rs.getString("type_name"));
                    monsterObj.addProperty("size", rs.getString("size_name"));
                    monsterObj.addProperty("alignment", rs.getString("alignment_name"));
                    monsterObj.addProperty("challengeRating", rs.getDouble("challenge_rating"));
                    monsterObj.addProperty("armorClass", rs.getInt("armor_class"));
                    monsterObj.addProperty("hpAverage", rs.getInt("hp_average"));
                    monsterObj.addProperty("hpFormula", rs.getString("hp_formula"));
                    monsterObj.addProperty("experiencePoints", rs.getInt("experience_points"));
                    monsterObj.addProperty("passivePerception", rs.getInt("passive_perception"));
                    monsterObj.addProperty("proficiencyBonus", rs.getInt("proficiency_bonus"));
                    monsterObj.addProperty("description", rs.getString("description"));
                    monsterObj.addProperty("isOfficial", rs.getInt("is_official") == 1);

                    monsterObj.add("stats", fetchStats(conn, monsterId));
                    monsterObj.add("savingThrows", fetchSavingThrows(conn, monsterId));
                    monsterObj.add("skills", fetchSkills(conn, monsterId));
                    monsterObj.add("speeds", fetchSpeeds(conn, monsterId));
                    monsterObj.add("senses", fetchSenses(conn, monsterId));
                    monsterObj.add("damageModifiers", fetchDamageModifiers(conn, monsterId));
                    monsterObj.add("conditionImmunities", fetchConditionImmunities(conn, monsterId));
                    monsterObj.add("languages", fetchLanguages(conn, monsterId));
                    monsterObj.add("abilities", fetchAbilities(conn, monsterId));
                    monsterObj.add("tags", fetchTags(conn, monsterId));

                    monstersArray.add(monsterObj);
                }
            }

            rootJson.add("monsters", monstersArray);

            File outputFile = new File("src/main/resources/bestiary_export.json");
            outputFile.getParentFile().mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            try (FileWriter writer = new FileWriter(outputFile)) {
                gson.toJson(rootJson, writer);
                System.out.println("Export completed successfully! Saved to '" + outputFile.getPath() + "'.");
            }

        } catch (SQLException e) {
            System.err.println("Database error during export: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File I/O error during export: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JsonObject fetchStats(Connection conn, long monsterId) throws SQLException {
        JsonObject stats = new JsonObject();
        String query = "SELECT strength, dexterity, constitution, intelligence, wisdom, charisma FROM MonsterStats WHERE monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.addProperty("strength", rs.getInt("strength"));
                    stats.addProperty("dexterity", rs.getInt("dexterity"));
                    stats.addProperty("constitution", rs.getInt("constitution"));
                    stats.addProperty("intelligence", rs.getInt("intelligence"));
                    stats.addProperty("wisdom", rs.getInt("wisdom"));
                    stats.addProperty("charisma", rs.getInt("charisma"));
                }
            }
        }
        return stats;
    }

    private static JsonArray fetchSavingThrows(Connection conn, long monsterId) {
        JsonArray saves = new JsonArray();
        String query = "SELECT ability_name, bonus FROM MonsterSavingThrow WHERE monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject saveObj = new JsonObject();
                    saveObj.addProperty("ability", rs.getString("ability_name"));
                    saveObj.addProperty("bonus", rs.getInt("bonus"));
                    saves.add(saveObj);
                }
            }
        } catch (SQLException ignored) {
        }
        return saves;
    }

    private static JsonArray fetchSkills(Connection conn, long monsterId) {
        JsonArray skills = new JsonArray();
        String query = "SELECT s.name AS skill_name, ms.bonus FROM MonsterSkill ms JOIN Skill s ON ms.skill_id = s.id WHERE ms.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject skillObj = new JsonObject();
                    skillObj.addProperty("skill", rs.getString("skill_name"));
                    skillObj.addProperty("bonus", rs.getInt("bonus"));
                    skills.add(skillObj);
                }
            }
        } catch (SQLException ignored) {
        }
        return skills;
    }

    private static JsonArray fetchSpeeds(Connection conn, long monsterId) throws SQLException {
        JsonArray speeds = new JsonArray();
        String query = "SELECT st.name AS type, ms.speed_ft FROM MonsterSpeed ms JOIN SpeedType st ON ms.speed_type_id = st.id WHERE ms.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject speedObj = new JsonObject();
                    speedObj.addProperty(rs.getString("type"), rs.getInt("speed_ft"));
                    speeds.add(speedObj);
                }
            }
        }
        return speeds;
    }

    private static JsonArray fetchSenses(Connection conn, long monsterId) throws SQLException {
        JsonArray senses = new JsonArray();
        String query = "SELECT st.name AS type, ms.range_ft FROM MonsterSense ms JOIN SenseType st ON ms.sense_type_id = st.id WHERE ms.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject senseObj = new JsonObject();
                    senseObj.addProperty("sense", rs.getString("type"));
                    senseObj.addProperty("rangeFt", rs.getInt("range_ft"));
                    senses.add(senseObj);
                }
            }
        }
        return senses;
    }

    private static JsonArray fetchDamageModifiers(Connection conn, long monsterId) {
        JsonArray modifiers = new JsonArray();
        String query = "SELECT mdm.modifier_type, dt.name AS damage_type " +
                "FROM MonsterDamageModifier mdm " +
                "JOIN DamageType dt ON mdm.damage_type_id = dt.id " +
                "WHERE mdm.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject modObj = new JsonObject();
                    modObj.addProperty("modifier", rs.getString("modifier_type"));
                    modObj.addProperty("damageType", rs.getString("damage_type"));
                    modifiers.add(modObj);
                }
            }
        } catch (SQLException ignored) {
        }
        return modifiers;
    }

    private static JsonArray fetchConditionImmunities(Connection conn, long monsterId) {
        JsonArray immunities = new JsonArray();
        String query = "SELECT c.name FROM MonsterConditionImmunity mci " +
                "JOIN Condition c ON mci.condition_id = c.id " +
                "WHERE mci.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    immunities.add(rs.getString("name"));
                }
            }
        } catch (SQLException ignored) {
        }
        return immunities;
    }

    private static JsonArray fetchLanguages(Connection conn, long monsterId) {
        JsonArray languages = new JsonArray();
        String query = "SELECT l.name FROM MonsterLanguage ml " +
                "JOIN Language l ON ml.language_id = l.id " +
                "WHERE ml.monster_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    languages.add(rs.getString("name"));
                }
            }
        } catch (SQLException ignored) {
        }
        return languages;
    }

    private static JsonArray fetchAbilities(Connection conn, long monsterId) throws SQLException {
        JsonArray abilities = new JsonArray();
        String query = "SELECT ability_type, name, description, recharge FROM MonsterAbility WHERE monster_id = ? ORDER BY display_order";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, monsterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject abilityObj = new JsonObject();
                    abilityObj.addProperty("category", rs.getString("ability_type"));
                    abilityObj.addProperty("name", rs.getString("name"));
                    abilityObj.addProperty("description", rs.getString("description"));
                    String recharge = rs.getString("recharge");
                    if (recharge != null) {
                        abilityObj.addProperty("recharge", recharge);
                    }
                    abilities.add(abilityObj);
                }
            }
        }
        return abilities;
    }

    private static JsonArray fetchTags(Connection conn, long monsterId) throws SQLException {
        JsonArray tags = new JsonArray();
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
}