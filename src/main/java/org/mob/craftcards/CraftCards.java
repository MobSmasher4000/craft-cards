package org.mob.craftcards;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.mob.craftcards.attribute.Attributes;
import org.mob.craftcards.attribute.CardCaseAttributeHandler;
import org.mob.craftcards.attribute.CardCaseEffectHandler;
import org.mob.craftcards.component.ModDataComponentTypes;
import org.mob.craftcards.item.ModItems;
import org.mob.craftcards.item.custom.CardCaseItem;
import org.mob.craftcards.network.OpenCardCasePayload;
import org.mob.craftcards.screen.CardCaseScreenHandler;
import org.mob.craftcards.screen.ModScreenHandlers;
import org.mob.craftcards.util.CardCaseInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CraftCards implements ModInitializer {
	public static final String MOD_ID = "craftcards";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int CASE_SIZE = 24;

	@Override
	public void onInitialize() {
        ModItems.registerAll();
        ModItemGroups.register();
        ModScreenHandlers.register();
        ModDataComponentTypes.registerDataComponentTypes();
        Attributes.init();

        PayloadTypeRegistry.playC2S().register(OpenCardCasePayload.ID, OpenCardCasePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenCardCasePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                PlayerEntity player = context.player();

                // Look for the Card Case in the Accessories slots
                AccessoriesCapability.getOptionally(player).ifPresent(capability -> {
                    // Find first item that is an instance of CardCaseItem
                    var container = capability.getEquipped(stack -> stack.getItem() instanceof CardCaseItem);

                    if (!container.isEmpty()) {
                        ItemStack caseStack = container.get(0).stack();

                        player.openHandledScreen(new NamedScreenHandlerFactory() {
                            @Override
                            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                                return new CardCaseScreenHandler(syncId, inv, new CardCaseInventory(caseStack, CASE_SIZE));
                            }
                            @Override
                            public Text getDisplayName() {
                                return Text.translatable("item.craftcards.card_case");
                            }
                        });
                    }
                });
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Get the Accessory Capability for this player
                var capability = AccessoriesCapability.get(player);
                if (capability != null) {

                    // Look for the Card Case specifically in the "belt" slot
                    // (Or use a general check if it can be equipped anywhere)
                    var equipped = capability.getEquipped(stack -> stack.getItem() instanceof CardCaseItem);

                    if (!equipped.isEmpty()) {
                        // We found it! Update attributes with the actual ItemStack
                        ItemStack caseStack = equipped.get(0).stack();
                        CardCaseAttributeHandler.updatePlayer(player, caseStack);
                        CardCaseEffectHandler.updatePlayer(player, caseStack);
                    } else {
                        // The slot is empty, trigger cleanup
                        CardCaseAttributeHandler.updatePlayer(player, ItemStack.EMPTY);
                        CardCaseEffectHandler.updatePlayer(player, ItemStack.EMPTY);
                    }
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            CardCaseAttributeHandler.removeFromCache(handler.getPlayer().getUuid());
            CardCaseEffectHandler.removeFromCache(handler.getPlayer().getUuid());
        });
    }
}