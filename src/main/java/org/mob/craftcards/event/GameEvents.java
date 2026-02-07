package org.mob.craftcards.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.mob.craftcards.CraftCards;
import org.mob.craftcards.attribute.CardCaseAttributeHandler;
import org.mob.craftcards.attribute.CardCaseEffectHandler;

@EventBusSubscriber(modid = CraftCards.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GameEvents {

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Handle cache cleanup
        CardCaseAttributeHandler.removeFromCache(event.getEntity().getUUID());
        CardCaseEffectHandler.removeFromCache(event.getEntity().getUUID());
    }
}