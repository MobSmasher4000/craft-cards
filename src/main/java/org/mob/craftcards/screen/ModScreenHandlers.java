package org.mob.craftcards.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.mob.craftcards.CraftCards;

public class ModScreenHandlers {

    public static final ScreenHandlerType<CardCaseScreenHandler> CARD_CASE =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(CraftCards.MOD_ID, "card_case"),
                    new ScreenHandlerType<>(CardCaseScreenHandler::new, FeatureSet.empty()) // client-side factory
            );

    public static void register() {
        CraftCards.LOGGER.info("Registered screen handlers");
    }
}
