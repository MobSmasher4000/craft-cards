package org.mob.craftcards.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;

public class CommonEvents {
    public static final CobblemonEvent<PokemonCatchRateEvent> POKEMON_CATCH_RATE = CobblemonEvent.of(CobblemonEvents.POKEMON_CATCH_RATE);
}
