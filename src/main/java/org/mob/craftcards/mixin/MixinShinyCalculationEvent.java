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

    @Shadow
    public abstract void addModifier(float modifier);

    @Inject(method = "calculate", at = @At("RETURN"), cancellable = true)
    private void calculate(ServerPlayerEntity player, CallbackInfoReturnable<Float> ci) {
        if (player == null){
            return;
        }
        float base_chance = ci.getReturnValue();
        float shinyBonus = CardCaseEffectHandler.getShinyBonus();
        float bonus = base_chance + (base_chance * shinyBonus);
        addModifier(shinyBonus);
        ci.setReturnValue(bonus);
    }

}
