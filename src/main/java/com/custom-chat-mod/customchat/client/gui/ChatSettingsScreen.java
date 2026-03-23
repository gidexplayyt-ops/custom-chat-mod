package com.yourname.customchat.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yourname.customchat.config.ChatConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChatSettingsScreen extends Screen {
    private EditBox posXField;
    private EditBox posYField;
    private EditBox durationField;
    private Button headsButton;
    private boolean dragging = false;
    
    public ChatSettingsScreen() {
        super(Component.literal("Настройки чата"));
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        
        // Поле X позиции
        this.posXField = new EditBox(this.font, centerX - 100, startY + 20, 60, 20, Component.literal("X"));
        this.posXField.setValue(String.valueOf(ChatConfig.getChatPositionX()));
        this.posXField.setMaxLength(3);
        this.addRenderableWidget(this.posXField);
        
        // Поле Y позиции
        this.posYField = new EditBox(this.font, centerX + 40, startY + 20, 60, 20, Component.literal("Y"));
        this.posYField.setValue(String.valueOf(ChatConfig.getChatPositionY()));
        this.posYField.setMaxLength(3);
        this.addRenderableWidget(this.posYField);
        
        // Длительность сообщений
        this.durationField = new EditBox(this.font, centerX - 30, startY + 60, 60, 20, Component.literal("Сек"));
        this.durationField.setValue(String.valueOf(ChatConfig.getMessageDuration()));
        this.durationField.setMaxLength(2);
        this.addRenderableWidget(this.durationField);
        
        // Кнопка показа голов
        this.headsButton = new Button(
            centerX - 75, startY + 100, 150, 20,
            Component.literal("Головы: " + (ChatConfig.showPlayerHeads() ? "§aВКЛ" : "§cВЫКЛ")),
            button -> {
                ChatConfig.setShowPlayerHeads(!ChatConfig.showPlayerHeads());
                button.setMessage(Component.literal("Головы: " + (ChatConfig.showPlayerHeads() ? "§aВКЛ" : "§cВЫКЛ")));
            }
        );
        this.addRenderableWidget(this.headsButton);
        
        // Кнопка сброса позиции
        this.addRenderableWidget(new Button(
            centerX - 75, startY + 130, 150, 20,
            Component.literal("Сбросить позицию"),
            button -> {
                ChatConfig.setChatPositionX(50);
                ChatConfig.setChatPositionY(85);
                this.posXField.setValue("50");
                this.posYField.setValue("85");
            }
        ));
        
        // Кнопка сохранить
        this.addRenderableWidget(new Button(
            centerX - 75, startY + 170, 150, 20,
            Component.literal("§aСохранить и закрыть"),
            button -> {
                saveSettings();
                this.minecraft.setScreen(null);
            }
        ));
    }
    
    private void saveSettings() {
        try {
            int x = Integer.parseInt(this.posXField.getValue());
            int y = Integer.parseInt(this.posYField.getValue());
            int duration = Integer.parseInt(this.durationField.getValue());
            
            ChatConfig.setChatPositionX(x);
            ChatConfig.setChatPositionY(y);
            ChatConfig.setMessageDuration(duration);
        } catch (NumberFormatException e) {
            // Игнорируем неверные значения
        }
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        
        // Заголовок
        drawCenteredString(poseStack, this.font, "§l§fНастройки Custom Chat", this.width / 2, 20, 0xFFFFFF);
        
        // Подписи полей
        drawString(poseStack, this.font, "§7X (0-100%):", this.width / 2 - 100, 55, 0xAAAAAA);
        drawString(poseStack, this.font, "§7Y (0-100%):", this.width / 2 + 40, 55, 0xAAAAAA);
        drawString(poseStack, this.font, "§7Длительность (сек):", this.width / 2 - 100, 95, 0xAAAAAA);
        
        // Превью позиции чата
        int previewX = (int) (this.width * ChatConfig.getChatPositionX() / 100.0);
        int previewY = (int) (this.height * ChatConfig.getChatPositionY() / 100.0);
        
        fill(poseStack, previewX - 60, previewY - 12, previewX + 60, previewY + 12, 0x80000000);
        drawCenteredString(poseStack, this.font, "§e⬛ §7Перетащи меня", previewX, previewY - 4, 0xFFFFFF);
        
        // Подсказка
        drawCenteredString(poseStack, this.font, "§8Папка: .minecraft/custom-chat-mod/", this.width / 2, this.height - 30, 0x666666);
        drawCenteredString(poseStack, this.font, "§8Головы NPC: heads.json", this.width / 2, this.height - 20, 0x666666);
        
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int previewX = (int) (this.width * ChatConfig.getChatPositionX() / 100.0);
        int previewY = (int) (this.height * ChatConfig.getChatPositionY() / 100.0);
        
        if (mouseX >= previewX - 60 && mouseX <= previewX + 60 &&
            mouseY >= previewY - 12 && mouseY <= previewY + 12) {
            dragging = true;
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            int newX = (int) (mouseX * 100 / this.width);
            int newY = (int) (mouseY * 100 / this.height);
            
            newX = Math.max(5, Math.min(95, newX));
            newY = Math.max(5, Math.min(95, newY));
            
            ChatConfig.setChatPositionX(newX);
            ChatConfig.setChatPositionY(newY);
            
            this.posXField.setValue(String.valueOf(newX));
            this.posYField.setValue(String.valueOf(newY));
            
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}