package com.yourname.customchat.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yourname.customchat.ChatHistory;
import com.yourname.customchat.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;

public class CustomChatScreen extends ChatScreen {
    private EditBox customInput;
    private CommandSuggestions commandSuggestions;
    private static final int MAX_CHAT_LINES = 10;
    private final String defaultText;
    private int inputY;

    public CustomChatScreen(String defaultText) {
        super(defaultText);
        this.defaultText = defaultText;
    }

    @Override
    protected void init() {
        super.init();
        
        ChatHistory.resetHistoryIndex();
        
        if (this.input != null) {
            this.input.setVisible(false);
        }
        
        int fieldWidth = 350;
        int fieldHeight = 20;
        int centerX = this.width / 2 - fieldWidth / 2;
        this.inputY = this.height / 2 + 120;
        
        this.customInput = new EditBox(
            this.font,
            centerX,
            this.inputY,
            fieldWidth,
            fieldHeight,
            Component.literal("")
        );
        
        this.customInput.setMaxLength(256);
        this.customInput.setBordered(true);
        this.customInput.setVisible(true);
        this.customInput.setTextColor(0xFFFFFF);
        this.customInput.setCanLoseFocus(false);
        this.customInput.setValue(this.defaultText);
        
        this.addRenderableWidget(this.customInput);
        this.setInitialFocus(this.customInput);
        
        // Подсказки команд (TAB)
        this.commandSuggestions = new CommandSuggestions(
            this.minecraft,
            this,
            this.customInput,
            this.font,
            false,
            false,
            1,
            10,
            true,
            -805306368
        );
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        fill(poseStack, 0, 0, this.width, this.height, 0x80000000);
        
        List<ChatHistory.ChatMessage> messages = ChatHistory.getRecentMessages(MAX_CHAT_LINES);
        
        int maxTextWidth = 200;
        for (ChatHistory.ChatMessage msg : messages) {
            String colorCode = getColorForSender(msg.sender);
            String formattedText = colorCode + msg.sender + "§7: §f" + msg.message;
            int textWidth = this.font.width(formattedText);
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
        }
        
        int boxWidth = Math.min(maxTextWidth + 50, this.width - 40);
        boxWidth = Math.max(boxWidth, 300);
        
        int lineHeight = 18;
        int displayCount = Math.min(messages.size(), MAX_CHAT_LINES);
        if (displayCount == 0) displayCount = 1;
        
        int boxHeight = displayCount * lineHeight + 60;
        int boxX = this.width / 2 - boxWidth / 2;
        int boxY = this.height / 2 - boxHeight / 2 + 40;
        
        fill(poseStack, boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xE0101010);
        
        String title = "§l§fЧат";
        int titleWidth = this.font.width(title);
        this.font.drawShadow(poseStack, title, this.width / 2f - titleWidth / 2f, boxY + 8, 0xFFFFFF);
        
        fill(poseStack, boxX + 10, boxY + 22, boxX + boxWidth - 10, boxY + 23, 0x40FFFFFF);
        
        renderChatHistory(poseStack, boxX + 10, boxY + 30, boxWidth - 20);
        
        this.customInput.render(poseStack, mouseX, mouseY, partialTick);
        
        // Рендер подсказок команд
        this.commandSuggestions.render(poseStack, mouseX, mouseY);
        
        String hint = "§7[Enter] §fОтправить  §7[Esc] §fЗакрыть  §7[↑↓] §fИстория  §7[Tab] §fКоманды";
        int hintWidth = this.font.width(hint);
        this.font.drawShadow(poseStack, hint, this.width / 2f - hintWidth / 2f, this.inputY + 25, 0xAAAAAA);
    }

    private void renderChatHistory(PoseStack poseStack, int x, int y, int maxWidth) {
        List<ChatHistory.ChatMessage> messages = ChatHistory.getRecentMessages(MAX_CHAT_LINES);
        
        if (messages.isEmpty()) {
            this.font.drawShadow(poseStack, "§7Сообщений пока нет...", x + 10, y + 10, 0x888888);
            return;
        }
        
        int lineHeight = 18;
        
        for (int i = 0; i < messages.size(); i++) {
            ChatHistory.ChatMessage msg = messages.get(i);
            int msgY = y + i * lineHeight;
            
            if (ChatConfig.showPlayerHeads()) {
                renderPlayerHead(poseStack, x, msgY);
            }
            
            String colorCode = getColorForSender(msg.sender);
            String formattedText = colorCode + msg.sender + "§7: §f" + msg.message;
            
            String trimmed = formattedText;
            if (this.font.width(trimmed) > maxWidth - 20) {
                while (this.font.width(trimmed + "...") > maxWidth - 20 && trimmed.length() > 0) {
                    trimmed = trimmed.substring(0, trimmed.length() - 1);
                }
                trimmed += "...";
            }
            
            int textX = ChatConfig.showPlayerHeads() ? x + 16 : x;
            this.font.drawShadow(poseStack, trimmed, textX, msgY + 3, 0xFFFFFF);
        }
    }
    
    private String getColorForSender(String sender) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String playerName = mc.player.getName().getString();
            if (sender.equals(playerName) || 
                (ChatConfig.hasCustomNickname() && sender.equals(ChatConfig.getCustomNickname()))) {
                return ChatConfig.getNicknameColor();
            }
        }
        return ChatConfig.getNameColor(sender);
    }

    private void renderPlayerHead(PoseStack poseStack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, mc.player.getSkinTextureLocation());
        RenderSystem.enableBlend();
        
        blit(poseStack, x, y, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
        blit(poseStack, x, y, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
        
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // TAB для автодополнения
        if (keyCode == 258) { // Tab
            this.commandSuggestions.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        
        // Стрелка вверх - предыдущее сообщение
        if (keyCode == 265) { // Up
            String prev = ChatHistory.getPreviousMessage();
            if (prev != null) {
                this.customInput.setValue(prev);
                this.customInput.moveCursorToEnd();
            }
            return true;
        }
        
        // Стрелка вниз - следующее сообщение
        if (keyCode == 264) { // Down
            String next = ChatHistory.getNextMessage();
            this.customInput.setValue(next);
            this.customInput.moveCursorToEnd();
            return true;
        }
        
        // Enter - отправка
        if (keyCode == 257 || keyCode == 335) {
            String message = this.customInput.getValue().trim();
            if (!message.isEmpty()) {
                ChatHistory.addSentMessage(message);
                if (this.input != null) {
                    this.input.setValue(message);
                }
                this.handleChatInput(message, true);
            }
            this.minecraft.setScreen(null);
            return true;
        }
        
        // Escape - закрыть
        if (keyCode == 256) {
            this.minecraft.setScreen(null);
            return true;
        }
        
        return this.customInput.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean result = this.customInput.charTyped(codePoint, modifiers);
        this.commandSuggestions.updateCommandInfo();
        return result;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestions.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.commandSuggestions.mouseScrolled(delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        this.customInput.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}