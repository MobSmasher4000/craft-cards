package org.mob.craftcards.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.mob.craftcards.attribute.CardCaseEffectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(EnchantmentHelper.class)
public class EnchantmentModifierMixin {

    @ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
    private static int modifyLootingAndFortune(int original, RegistryEntry<Enchantment> enchantment, ItemStack stack) {

        if (enchantment.matchesKey(Enchantments.FORTUNE)) {
            int bonus = CardCaseEffectHandler.getFortuneBonus();
            return original + bonus;
        }

        if (enchantment.matchesKey(Enchantments.LOOTING)){
            int bonus = CardCaseEffectHandler.getLootingBonus();
            return original + bonus;
        }

        return original;
    }
}