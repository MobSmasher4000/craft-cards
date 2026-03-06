package org.mob.craftcards.mixin;

import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.mob.craftcards.attribute.CardCaseEffectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShinyChanceCalculationEvent.class)
public abstract class MixinShinyCalculationEvent {

    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void calculate(ServerPlayerEntity player, CallbackInfoReturnable<Float> ci) {
        if (player == null){
            return;
        }
        float base_chance = ci.getReturnValue();
        float shinyBonus = CardCaseEffectHandler.getShinyBonus();
        if (shinyBonus <= 0.0f) {
            return;
        }
        float bonus = base_chance / (1.0f + shinyBonus);
        bonus = Math.max(1.0f, bonus);
//        System.out.println("base shiny chance : "+base_chance);
//        System.out.println("shiny chance : "+shinyBonus);
//        System.out.println("bonus shiny chance : "+bonus);
        ci.setReturnValue(bonus);
//        ci.setReturnValue(1F);
    }

}
