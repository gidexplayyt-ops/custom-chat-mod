package com.yourname.customchat.config;

import java.util.HashMap;
import java.util.Map;

public class ChatConfig {
    private static final Map<String, String> nameColors = new HashMap<>();
    private static String customNickname = null;
    private static String nicknameColor = "§f";
    
    static {
        nameColors.put("Торговец", "§6");
        nameColors.put("Стражник", "§c");
        nameColors.put("Мудрец", "§5");
        nameColors.put("Квестодатель", "§b");
        nameColors.put("Жрец", "§e");
        nameColors.put("Кузнец", "§7");
    }
    
    public static String getNameColor(String name) {
        return nameColors.getOrDefault(name, "§f");
    }
    
    public static void setNameColor(String name, String colorCode) {
        nameColors.put(name, colorCode);
    }
    
    public static void setCustomNickname(String nickname) {
        customNickname = nickname;
    }
    
    public static String getCustomNickname() {
        return customNickname;
    }
    
    public static boolean hasCustomNickname() {
        return customNickname != null && !customNickname.isEmpty();
    }
    
    public static void clearCustomNickname() {
        customNickname = null;
    }
    
    public static void setNicknameColor(String colorCode) {
        nicknameColor = colorCode;
    }
    
    public static String getNicknameColor() {
        return nicknameColor;
    }
    
    public static String parseColorName(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "red" -> "§c";
            case "dark_red" -> "§4";
            case "blue" -> "§9";
            case "aqua" -> "§b";
            case "green" -> "§a";
            case "dark_green" -> "§2";
            case "yellow" -> "§e";
            case "gold" -> "§6";
            case "purple" -> "§5";
            case "light_purple" -> "§d";
            case "gray" -> "§7";
            case "dark_gray" -> "§8";
            case "white" -> "§f";
            case "black" -> "§0";
            default -> "§f";
        };
    }
}