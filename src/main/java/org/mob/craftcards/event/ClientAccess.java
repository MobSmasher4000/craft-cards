package org.mob.craftcards.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.network.OpenCardCasePayload;

// This class holds the static KeyMapping reference
public class ClientAccess {
    public static KeyMapping openCardCaseKey;

    /**
     * MOD BUS SUBSCRIBER
     * Handles registration (Screens, Keys)
     */
    @EventBusSubscriber(modid = CraftCards.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            openCardCaseKey = new KeyMapping(
                    "key.craftcards.open_case",       // Translation key
                    InputConstants.Type.KEYSYM,       // Key Type
                    GLFW.GLFW_KEY_G,                  // Default Key
                    "category.craftcards.general"     // Category
            );
            event.register(openCardCaseKey);
        }
    }

    /**
     * GAME BUS SUBSCRIBER
     * Handles in-game events (Ticks, Input checks)
     */
    @EventBusSubscriber(modid = CraftCards.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameEvents {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            // Check if the key was pressed
            while (openCardCaseKey.consumeClick()) {
                // Send payload to server
                PacketDistributor.sendToServer(new OpenCardCasePayload());
            }
        }
    }
}