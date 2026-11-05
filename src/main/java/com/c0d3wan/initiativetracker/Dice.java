package com.c0d3wan.initiativetracker;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice {


    public int D4(){return die(1,4);}
    public int D6(){return die(1,6);}
    public int D8(){return die(1,8);}
    public int D10(){return die(1,10);}
    public int D100(){return die(1,100);}
    public int D12(){return die(1,12);}
    public int D20(){return die(1,20);}

    public int die(int x, int y){return ThreadLocalRandom.current().nextInt(x,y+1);}

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
