package org.mob.craftcards.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.item.custom.CardCaseItem;
import top.theillusivec4.curios.api.CuriosApi;

public class PacketHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CraftCards.MOD_ID);

        registrar.playBidirectional(
                OpenCardCasePayload.ID,
                OpenCardCasePayload.CODEC,
                PacketHandler::handleOpenCardCase
        );
    }

    private static void handleOpenCardCase(OpenCardCasePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {

                // Use Curios API to find the card case in card case slot
                var curioStack = CuriosApi.getCuriosHelper()
                        .findCurio(player, "cardcase", 0);

                if (curioStack.isPresent()) {
                    // Open the menu using the helper method we made in Step 2
                    CardCaseItem.openMenu(player, curioStack.get().stack());
                }
            }
        });
    }
}