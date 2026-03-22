package com.yourname.customchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.yourname.customchat.ChatHistory;
import com.yourname.customchat.config.ChatConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ClearChatCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("clearchat")
                .executes(ClearChatCommand::executeClear)
        );
        
        dispatcher.register(
            Commands.literal("chatname")
                .then(Commands.literal("reset")
                    .executes(ClearChatCommand::executeResetName))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes(ClearChatCommand::executeSetName))
                .executes(ClearChatCommand::executeShowName)
        );
        
        dispatcher.register(
            Commands.literal("chatcolor")
                .then(Commands.argument("color", StringArgumentType.word())
                    .executes(ClearChatCommand::executeSetColor))
                .executes(ClearChatCommand::executeShowColors)
        );
    }
    
    private static int executeClear(CommandContext<CommandSourceStack> context) {
        ChatHistory.clearChat();
        context.getSource().sendSuccess(Component.literal("§aЧат очищен!"), false);
        return 1;
    }
    
    private static int executeSetName(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        ChatConfig.setCustomNickname(name);
        String color = ChatConfig.getNicknameColor();
        context.getSource().sendSuccess(
            Component.literal("§aТвой ник в чате: " + color + name), false);
        return 1;
    }
    
    private static int executeShowName(CommandContext<CommandSourceStack> context) {
        if (ChatConfig.hasCustomNickname()) {
            String color = ChatConfig.getNicknameColor();
            context.getSource().sendSuccess(
                Component.literal("§7Текущий ник: " + color + ChatConfig.getCustomNickname()), false);
        } else {
            context.getSource().sendSuccess(
                Component.literal("§7Ник не установлен. Используй: §f/chatname <ник>"), false);
        }
        return 1;
    }
    
    private static int executeResetName(CommandContext<CommandSourceStack> context) {
        ChatConfig.clearCustomNickname();
        context.getSource().sendSuccess(
            Component.literal("§aНик сброшен"), false);
        return 1;
    }
    
    private static int executeSetColor(CommandContext<CommandSourceStack> context) {
        String colorName = StringArgumentType.getString(context, "color");
        String colorCode = ChatConfig.parseColorName(colorName);
        ChatConfig.setNicknameColor(colorCode);
        context.getSource().sendSuccess(
            Component.literal("§aЦвет ника: " + colorCode + colorName), false);
        return 1;
    }
    
    private static int executeShowColors(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(Component.literal("§7Цвета:"), false);
        context.getSource().sendSuccess(Component.literal(
            "§cred §4dark_red §9blue §baqua §agreen §2dark_green"
        ), false);
        context.getSource().sendSuccess(Component.literal(
            "§eyellow §6gold §5purple §dlight_purple §7gray §8dark_gray §fwhite"
        ), false);
        return 1;
    }
}