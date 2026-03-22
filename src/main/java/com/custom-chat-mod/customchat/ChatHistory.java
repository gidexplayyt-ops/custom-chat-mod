package com.yourname.customchat;

import com.yourname.customchat.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory {
    private static final List<ChatMessage> messages = new ArrayList<>();
    private static final int MAX_MESSAGES = 50;
    
    public static void addMessage(Component content) {
        String text = content.getString();
        String sender = "Система";
        String message = text;
        
        if (text.startsWith("<") && text.contains(">")) {
            int endBracket = text.indexOf(">");
            sender = text.substring(1, endBracket);
            message = text.substring(endBracket + 1).trim();
            
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && sender.equals(mc.player.getName().getString())) {
                if (ChatConfig.hasCustomNickname()) {
                    sender = ChatConfig.getCustomNickname();
                }
            }
        }
        
        for (ChatMessage msg : messages) {
            msg.hideNow();
        }
        
        messages.add(0, new ChatMessage(sender, message, System.currentTimeMillis()));
        
        while (messages.size() > MAX_MESSAGES) {
            messages.remove(messages.size() - 1);
        }
    }
    
    public static void clearChat() {
        messages.clear();
    }
    
    public static List<ChatMessage> getMessages() {
        return messages;
    }
    
    public static List<ChatMessage> getRecentMessages(int count) {
        return messages.subList(0, Math.min(count, messages.size()));
    }
    
    public static class ChatMessage {
        public final String sender;
        public final String message;
        public long timestamp;
        
        public ChatMessage(String sender, String message, long timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public void hideNow() {
            this.timestamp = System.currentTimeMillis() - 15000;
        }
        
        public boolean isRecent() {
            return System.currentTimeMillis() - timestamp < 10000;
        }
    }
}