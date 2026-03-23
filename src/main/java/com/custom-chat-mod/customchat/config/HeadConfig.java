package com.yourname.customchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class HeadConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = Paths.get(Minecraft.getInstance().gameDirectory.getPath(), "custom-chat-mod");
    private static final Path HEADS_FILE = CONFIG_DIR.resolve("heads.json");
    
    // Имя NPC -> UUID или ник игрока для скина
    private static Map<String, String> npcHeads = new HashMap<>();
    
    static {
        loadHeads();
    }
    
    public static void loadHeads() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
            
            if (Files.exists(HEADS_FILE)) {
                Reader reader = Files.newBufferedReader(HEADS_FILE);
                npcHeads = GSON.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                reader.close();
                if (npcHeads == null) {
                    npcHeads = new HashMap<>();
                }
            } else {
                // Примеры по умолчанию
                npcHeads.put("Торговец", "MHF_Villager");
                npcHeads.put("Стражник", "MHF_Steve");
                npcHeads.put("Мудрец", "MHF_Villager");
                npcHeads.put("Система", "MHF_Question");
                saveHeads();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveHeads() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
            
            Writer writer = Files.newBufferedWriter(HEADS_FILE);
            GSON.toJson(npcHeads, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getHeadSkin(String name) {
        return npcHeads.getOrDefault(name, null);
    }
    
    public static void setHeadSkin(String name, String skinName) {
        npcHeads.put(name, skinName);
        saveHeads();
    }
    
    public static void removeHeadSkin(String name) {
        npcHeads.remove(name);
        saveHeads();
    }
    
    public static Map<String, String> getAllHeads() {
        return new HashMap<>(npcHeads);
    }
}