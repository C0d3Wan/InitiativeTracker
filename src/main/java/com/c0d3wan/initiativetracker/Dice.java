package com.c0d3wan.initiativetracker;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides random number generation utilities for rolling standard D&D dice
 * and parsing complex dice formulas (e.g., "2d8 + 3").
 */
public class Dice {

    /** Rolls a standard 4-sided die. */
    public int D4(){return die(1,4);}
    /** Rolls a standard 6-sided die. */
    public int D6(){return die(1,6);}
    /** Rolls a standard 8-sided die. */
    public int D8(){return die(1,8);}
    /** Rolls a standard 10-sided die. */
    public int D10(){return die(1,10);}
    /** Rolls a standard 100-sided percentage die. */
    public int D100(){return die(1,100);}
    /** Rolls a standard 12-sided die. */
    public int D12(){return die(1,12);}
    /** Rolls a standard 20-sided die. */
    public int D20(){return die(1,20);}

    /**
     * Generates a random integer inclusive between x and y.
     *
     * @param x minimum value
     * @param y maximum value
     * @return random roll result
     */
    public int die(int x, int y){return ThreadLocalRandom.current().nextInt(x,y+1);}

    /**
     * Parses and evaluates a D&D dice formula string (e.g., "3d6 + 5").
     *
     * @param formula the dice formula string
     * @return the calculated integer total (guaranteed at least 1)
     */
    public int rollFormula(String formula) {
        if (formula == null || formula.isBlank()) {
            return 1;
        }

        formula = formula.replaceAll("\\s+", "").toLowerCase();
        int total = 0;

        Pattern dicePattern = Pattern.compile("([+-]?\\d+)d(\\d+)");
        Matcher diceMatcher = dicePattern.matcher(formula);

        while (diceMatcher.find()) {
            int count = Integer.parseInt(diceMatcher.group(1));
            int sides = Integer.parseInt(diceMatcher.group(2));
            for (int i = 0; i < Math.abs(count); i++) {
                int roll = die(1, sides);
                if (count < 0) {
                    total -= roll;
                } else {
                    total += roll;
                }
            }
        }

        String remaining = formula.replaceAll("([+-]?\\d+d\\d+)", "");
        Pattern modPattern = Pattern.compile("([+-]?\\d+)");
        Matcher modMatcher = modPattern.matcher(remaining);

        while (modMatcher.find()) {
            total += Integer.parseInt(modMatcher.group(1));
        }

        return Math.max(1, total);
    }
}