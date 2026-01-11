package com.productreview.util;

public class ProductNameUtil {
    
    private static final String TURKISH_CHARS = "çğıöşüÇĞİÖŞÜ";
    private static final String ENGLISH_CHARS = "cgiosuCGIOSU";
    
    public static String normalizeProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        // Remove Turkish characters
        String normalized = replaceTurkishChars(name.trim());
        
        // Capitalize first letter of each word
        return capitalizeWords(normalized);
    }
    
    private static String replaceTurkishChars(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int index = TURKISH_CHARS.indexOf(c);
            if (index != -1) {
                result.append(ENGLISH_CHARS.charAt(index));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static String capitalizeWords(String text) {
        if (text.isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                if (i < words.length - 1) {
                    result.append(" ");
                }
            }
        }
        
        return result.toString();
    }
    
    public static boolean containsTurkishChars(String text) {
        if (text == null) {
            return false;
        }
        
        for (int i = 0; i < text.length(); i++) {
            if (TURKISH_CHARS.indexOf(text.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }
}
