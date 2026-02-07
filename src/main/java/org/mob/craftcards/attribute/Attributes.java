package org.mob.craftcards.attribute;

import com.cobblemon.mod.common.api.Priority;
import org.mob.craftcards.event.CommonEvents;

public class Attributes {

    public static void init() {
        CommonEvents.POKEMON_CATCH_RATE.register(event -> {
            float current = event.getCatchRate();
            float captureBonus = CardCaseEffectHandler.getCaptureBonus();
            float rate = current + (current * captureBonus);
            if(current != rate) {
                event.setCatchRate(rate);
            }
        }, Priority.LOWEST);
    }

}