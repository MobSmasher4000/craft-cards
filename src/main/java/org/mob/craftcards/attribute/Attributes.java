package org.mob.craftcards.attribute;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;

public class Attributes {

    public static void init() {
        CobblemonEvents.POKEMON_CATCH_RATE.subscribe(Priority.LOWEST,pokemonCatchRateEvent ->{
            float current = pokemonCatchRateEvent.getCatchRate();
            float captureBonus = CardCaseEffectHandler.getCaptureBonus();
            float rate = current + (current * captureBonus);
            if (current != rate){
                pokemonCatchRateEvent.setCatchRate(rate);
            }
        });
    }

}