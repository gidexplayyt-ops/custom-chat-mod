package com.yourname.customchat;

import com.mojang.logging.LogUtils;
import com.yourname.customchat.client.KeyBindings;
import com.yourname.customchat.client.gui.ChatSettingsScreen;
import com.yourname.customchat.client.gui.CustomChatScreen;
import com.yourname.customchat.client.ChatHudOverlay;
import com.yourname.customchat.command.ClearChatCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CustomChatMod.MODID)
public class CustomChatMod {
    public static final String MODID = "customchat";
    public static final String VERSION = "2.0.0";
    public static final String AUTHOR = "GidexPlayYT";
    
    private static final Logger LOGGER = LogUtils.getLogger();

    public CustomChatMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerKeyBindings);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Custom Chat Mod v{} by {} загружен!", VERSION, AUTHOR);
    }
    
    private void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.OPEN_SETTINGS);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        
        @SubscribeEvent
        public static void onScreenOpen(ScreenEvent.Opening event) {
            if (event.getScreen() instanceof ChatScreen && !(event.getScreen() instanceof CustomChatScreen)) {
                event.setCanceled(true);
                Minecraft.getInstance().setScreen(new CustomChatScreen(""));
            }
        }
        
        @SubscribeEvent
        public static void onChatReceived(ClientChatReceivedEvent event) {
            ChatHistory.addMessage(event.getMessage());
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderChatPre(RenderGuiOverlayEvent.Pre event) {
            if (event.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type()) {
                event.setCanceled(true);
            }
        }
        
        @SubscribeEvent
        public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
                Minecraft mc = Minecraft.getInstance();
                if (!(mc.screen instanceof CustomChatScreen) && !(mc.screen instanceof ChatSettingsScreen)) {
                    ChatHudOverlay.render(event.getPoseStack(), mc);
                }
            }
        }
        
        @SubscribeEvent
        public static void onRegisterCommands(RegisterClientCommandsEvent event) {
            ClearChatCommand.register(event.getDispatcher());
        }
        
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null && KeyBindings.OPEN_SETTINGS.consumeClick()) {
                    mc.setScreen(new ChatSettingsScreen());
                }
            }
        }
    }
}