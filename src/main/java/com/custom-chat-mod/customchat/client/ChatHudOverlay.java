package com.yourname.customchat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yourname.customchat.ChatHistory;
import com.yourname.customchat.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;

import java.util.List;

public class ChatHudOverlay {
    private static final int MAX_VISIBLE = 1;
    
    public static void render(PoseStack poseStack, Minecraft mc) {
        if (mc.player == null || mc.options.hideGui) return;
        
        List<ChatHistory.ChatMessage> messages = ChatHistory.getRecentMessages(MAX_VISIBLE);
        if (messages.isEmpty()) return;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Динамическая позиция из конфига
        int posX = (int) (screenWidth * ChatConfig.getChatPositionX() / 100.0);
        int posY = (int) (screenHeight * ChatConfig.getChatPositionY() / 100.0);
        
        int lineHeight = 16;
        int padding = 8;
        int headSize = ChatConfig.showPlayerHeads() ? 14 : 0;
        
        int visibleCount = 0;
        int maxTextWidth = 0;
        
        for (ChatHistory.ChatMessage msg : messages) {
            if (msg.isRecent()) {
                visibleCount++;
                String colorCode = getColorForSender(msg.sender, mc);
                String formattedText = colorCode + msg.sender + "§7: §f" + msg.message;
                int textWidth = mc.font.width(formattedText);
                if (textWidth > maxTextWidth) {
                    maxTextWidth = textWidth;
                }
            }
        }
        
        if (visibleCount == 0) return;
        
        int boxWidth = maxTextWidth + headSize + padding * 3;
        if (boxWidth < 150) boxWidth = 150;
        if (boxWidth > screenWidth - 40) boxWidth = screenWidth - 40;
        
        int boxHeight = visibleCount * lineHeight + padding * 2;
        int boxX = posX - boxWidth / 2;
        int boxY = posY - boxHeight / 2;
        
        // Ограничиваем границы экрана
        if (boxX < 5) boxX = 5;
        if (boxX + boxWidth > screenWidth - 5) boxX = screenWidth - boxWidth - 5;
        if (boxY < 5) boxY = 5;
        if (boxY + boxHeight > screenHeight - 5) boxY = screenHeight - boxHeight - 5;
        
        GuiComponent.fill(poseStack, boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xB0101010);
        
        int y = boxY + padding;
        for (ChatHistory.ChatMessage msg : messages) {
            if (!msg.isRecent()) continue;
            
            if (ChatConfig.showPlayerHeads()) {
                renderPlayerHead(poseStack, mc, boxX + padding, y);
            }
            
            String colorCode = getColorForSender(msg.sender, mc);
            String formattedText = colorCode + msg.sender + "§7: §f" + msg.message;
            
            int textX = boxX + padding + headSize;
            mc.font.drawShadow(poseStack, formattedText, textX, y + 2, 0xFFFFFF);
            
            y += lineHeight;
        }
    }
    
    private static String getColorForSender(String sender, Minecraft mc) {
        if (mc.player != null) {
            String playerName = mc.player.getName().getString();
            if (sender.equals(playerName) || 
                (ChatConfig.hasCustomNickname() && sender.equals(ChatConfig.getCustomNickname()))) {
                return ChatConfig.getNicknameColor();
            }
        }
        return ChatConfig.getNameColor(sender);
    }
    
    private static void renderPlayerHead(PoseStack poseStack, Minecraft mc, int x, int y) {
        if (mc.player == null) return;
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, mc.player.getSkinTextureLocation());
        RenderSystem.enableBlend();
        
        GuiComponent.blit(poseStack, x, y, 10, 10, 8.0F, 8.0F, 8, 8, 64, 64);
        GuiComponent.blit(poseStack, x, y, 10, 10, 40.0F, 8.0F, 8, 8, 64, 64);
        
        RenderSystem.disableBlend();
    }
}