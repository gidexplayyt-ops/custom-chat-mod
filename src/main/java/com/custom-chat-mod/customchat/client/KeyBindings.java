package com.yourname.customchat.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.customchat.category";
    
    public static final KeyMapping OPEN_SETTINGS = new KeyMapping(
        "key.customchat.open_settings",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_F7,
        CATEGORY
    );
}