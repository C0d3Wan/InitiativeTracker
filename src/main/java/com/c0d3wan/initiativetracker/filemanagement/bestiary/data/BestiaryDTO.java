package com.c0d3wan.initiativetracker.filemanagement.bestiary.data;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object container representing the overall structure of the bestiary JSON resource file.
 */
public class BestiaryDTO {
    private String version;
    private List<Monster> monsters;

    /** Default constructor. */
    public BestiaryDTO() {}

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<Monster> getMonsters() { return monsters; }
    public void setMonsters(List<Monster> monsters) { this.monsters = monsters; }

    /**
     * Represents an individual D&D 5e monster record, including its stats, resistances, and actions.
     */
    public static class Monster {
        private String name;
        private String group_name;
        private String type;
        private String size;
        private String alignment;
        private int armorClass;

        private HitPoints hitPoints;
        private Map<String, Integer> speed;
        private Map<String, Integer> stats;

        private String challengeRating;
        private int experiencePoints;

        private Map<String, Integer> savingThrows;
        private Map<String, Integer> skills;

        private List<String> damageResistances;
        private List<String> damageVulnerabilities;
        private List<String> damageImmunities;
        private List<String> conditionImmunities;
        private List<String> senses;
        private List<String> languages;

        private List<Feature> traits;
        private List<Feature> actions;
        private List<Feature> reactions;
        private ActionGroup legendary_actions;
        private ActionGroup lair_actions;

        /** Default constructor. */
        public Monster() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getGroupName() { return group_name; }
        public void setGroup(String group) { this.group_name = group; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getAlignment() { return alignment; }
        public void setAlignment(String alignment) { this.alignment = alignment; }

        public int getArmorClass() { return armorClass; }
        public void setArmorClass(int armorClass) { this.armorClass = armorClass; }

        public HitPoints getHitPoints() { return hitPoints; }
        public void setHitPoints(HitPoints hitPoints) { this.hitPoints = hitPoints; }

        public Map<String, Integer> getSpeed() { return speed; }
        public void setSpeed(Map<String, Integer> speed) { this.speed = speed; }

        public Map<String, Integer> getStats() { return stats; }
        public void setStats(Map<String, Integer> stats) { this.stats = stats; }

        public String getChallengeRating() { return challengeRating; }
        public void setChallengeRating(String challengeRating) { this.challengeRating = challengeRating; }

        public int getExperiencePoints() { return experiencePoints; }
        public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

        public Map<String, Integer> getSavingThrows() { return savingThrows; }
        public void setSavingThrows(Map<String, Integer> savingThrows) { this.savingThrows = savingThrows; }

        public Map<String, Integer> getSkills() { return skills; }
        public void setSkills(Map<String, Integer> skills) { this.skills = skills; }

        public List<String> getDamageResistances() { return damageResistances; }
        public void setDamageResistances(List<String> damageResistances) { this.damageResistances = damageResistances; }

        public List<String> getDamageVulnerabilities() { return damageVulnerabilities; }
        public void setDamageVulnerabilities(List<String> damageVulnerabilities) { this.damageVulnerabilities = damageVulnerabilities; }

        public List<String> getDamageImmunities() { return damageImmunities; }
        public void setDamageImmunities(List<String> damageImmunities) { this.damageImmunities = damageImmunities; }

        public List<String> getConditionImmunities() { return conditionImmunities; }
        public void setConditionImmunities(List<String> conditionImmunities) { this.conditionImmunities = conditionImmunities; }

        public List<String> getSenses() { return senses; }
        public void setSenses(List<String> senses) { this.senses = senses; }

        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }

        public List<Feature> getTraits() { return traits; }
        public void setTraits(List<Feature> traits) { this.traits = traits; }

        public List<Feature> getActions() { return actions; }
        public void setActions(List<Feature> actions) { this.actions = actions; }

        public List<Feature> getReactions() { return reactions; }
        public void setReactions(List<Feature> reactions) { this.reactions = reactions; }

        public ActionGroup getLegendaryActions() { return legendary_actions; }
        public void setLegendaryActions(ActionGroup legendary_actions) { this.legendary_actions = legendary_actions; }

        public ActionGroup getLairActions() { return lair_actions; }
        public void setLairActions(ActionGroup lair_actions) { this.lair_actions = lair_actions; }
    }

    /**
     * Represents a monster's hit points, including average value and dice formula.
     */
    public static class HitPoints {
        private int average;
        private String formula;

        /** Default constructor. */
        public HitPoints() {}

        public int getAverage() { return average; }
        public void setAverage(int average) { this.average = average; }

        public String getFormula() { return formula; }
        public void setFormula(String formula) { this.formula = formula; }
    }

    /**
     * Represents a specific feature, trait, or action belonging to a monster.
     */
    public static class Feature {
        private String name;
        private String description;

        /** Default constructor. */
        public Feature() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * Represents a grouped section of actions (such as Legendary or Lair actions) with a description.
     */
    public static class ActionGroup {
        private String description;
        private List<Feature> actions;

        /** Default constructor. */
        public ActionGroup() {}

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<Feature> getActions() { return actions; }
        public void setActions(List<Feature> actions) { this.actions = actions; }
    }
}