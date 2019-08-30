package fr.syrows.smartcommands.utils;

import fr.syrows.smartcommands.SmartCommandsAPI;
import org.bukkit.ChatColor;

public class Utils {

    public static String parseColors(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static boolean isInt(String str) {

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getLevenshteinDistance(String str1, String str2) {

        int n = str1.length(), m = str2.length();

        int[][] array = new int[n + 1][m + 1];

        for(int l = 0; l < n + 1; l++) array[l][0] = l;

        for(int c = 0; c < m + 1; c++) array[0][c] = c;

        for(int l = 1; l < n + 1; l++) {

            for(int c = 1; c < m + 1; c++) {

                int substitutionCost = str1.charAt(l - 1) == str2.charAt(c - 1) ? 0 : 1;

                int deletion = array[l - 1][c] + 1, insertion = array[l][c - 1] + 1,
                        substitution = array[l - 1][c - 1] + substitutionCost;

                array[l][c] = getMin(deletion, insertion, substitution);
            }
        }
        return array[n][m];
    }

    public static double getPercentageOfSimilarity(String str1, String str2) {
        return (1.0 - (double) getLevenshteinDistance(str1, str2) / Math.max(str1.length(), str2.length())) * 100;
    }

    private static int getMin(int a, int b, int c) {

        if(a < b && a < c) return a;

        if(b < a && b < c) return b;

        if(c < a && c < b) return c;

        return a;
    }
}
