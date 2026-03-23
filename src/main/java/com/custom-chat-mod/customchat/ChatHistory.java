package com.yourname.customchat;

import com.yourname.customchat.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ChatHistory {
    private static final List<ChatMessage> messages = new ArrayList<>();
    private static final List<String> sentMessages = new ArrayList<>();
    private static final int MAX_MESSAGES = 50;
    private static final int MAX_SENT_HISTORY = 100;
    private static int sentHistoryIndex = -1;
    
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
    
    // История отправленных сообщений
    public static void addSentMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;
        
        // Не добавляем дубликаты подряд
        if (!sentMessages.isEmpty() && sentMessages.get(0).equals(message)) {
            return;
        }
        
        sentMessages.add(0, message);
        
        while (sentMessages.size() > MAX_SENT_HISTORY) {
            sentMessages.remove(sentMessages.size() - 1);
        }
        
        resetHistoryIndex();
    }
    
    public static void resetHistoryIndex() {
        sentHistoryIndex = -1;
    }
    
    public static String getPreviousMessage() {
        if (sentMessages.isEmpty()) return null;
        
        sentHistoryIndex++;
        if (sentHistoryIndex >= sentMessages.size()) {
            sentHistoryIndex = sentMessages.size() - 1;
        }
        
        return sentMessages.get(sentHistoryIndex);
    }
    
    public static String getNextMessage() {
        if (sentMessages.isEmpty() || sentHistoryIndex < 0) return "";
        
        sentHistoryIndex--;
        if (sentHistoryIndex < 0) {
            sentHistoryIndex = -1;
            return "";
        }
        
        return sentMessages.get(sentHistoryIndex);
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
            int duration = ChatConfig.getMessageDuration() * 1000;
            return System.currentTimeMillis() - timestamp < duration;
        }
    }
}