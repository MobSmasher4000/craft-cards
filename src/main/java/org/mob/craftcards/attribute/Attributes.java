package org.mob.craftcards.attribute;

import com.cobblemon.mod.common.api.Priority;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.mob.craftcards.event.CommonEvents;

import java.util.UUID;

public class Attributes {

    public static void init() {
        CommonEvents.POKEMON_CATCH_RATE.register(event -> {
            LivingEntity entity = event.getThrower();
            float current = event.getCatchRate();
            float captureBonus = CardCaseEffectHandler.getCaptureBonus();
            float rate = current + (current * captureBonus);

            if(current != rate) {
                event.setCatchRate(rate);
            }
        }, Priority.LOWEST);
    }

}