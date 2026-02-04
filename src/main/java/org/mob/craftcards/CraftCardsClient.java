package org.mob.craftcards;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.mob.craftcards.network.OpenCardCasePayload;
import org.mob.craftcards.screen.CardCaseScreen;
import org.mob.craftcards.screen.ModScreenHandlers;

public class CraftCardsClient implements ClientModInitializer {
    public static KeyBinding openCardCaseKey;

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CARD_CASE, CardCaseScreen::new);

        openCardCaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.craftcards.open_case", // Translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G, // Default key (G)
                "category.craftcards.general" // Category in controls menu
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openCardCaseKey.wasPressed()) {
                // Send a packet to the server to request opening the case
                ClientPlayNetworking.send(new OpenCardCasePayload());
            }
        });
    }
}
