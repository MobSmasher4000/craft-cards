package org.mob.craftcards;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.mob.craftcards.attribute.Attributes;
import org.mob.craftcards.component.ModDataComponentTypes;
import org.mob.craftcards.item.ModItemGroups;
import org.mob.craftcards.item.ModItems;
import org.mob.craftcards.loot.ModLootModifiers;
import org.mob.craftcards.network.PacketHandler;
import org.mob.craftcards.screen.CardCaseScreen;
import org.mob.craftcards.screen.ModMenuTypes;

@Mod(CraftCards.MOD_ID)
public class CraftCards {
    public static final String MOD_ID = "craftcards";
    public static final int CASE_SIZE = 25;

    public CraftCards(IEventBus modBus) {
        ModItems.register(modBus);
        ModItemGroups.register(modBus);
        ModMenuTypes.register(modBus);
        ModDataComponentTypes.register(modBus);
        Attributes.init();
        ModLootModifiers.register(modBus);

        // 3. Register Networking
        modBus.addListener(PacketHandler::register);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.CARD_CASE.get(), CardCaseScreen::new);
        }
    }

}