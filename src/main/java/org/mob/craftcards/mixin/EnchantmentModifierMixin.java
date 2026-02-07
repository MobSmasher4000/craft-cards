package org.mob.craftcards.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.mob.craftcards.attribute.CardCaseEffectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(EnchantmentHelper.class)
public class EnchantmentModifierMixin {

    /**
     * TARGET: Fortune (Block Drops)
     * This method is called whenever a block is mined to calculate drops.
     */
    @ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
    private static int modifyFortuneEnchantLevel(int original, Holder<Enchantment> enchantment, ItemStack stack) {

        if (enchantment.is(Enchantments.FORTUNE)){
            int bonus = CardCaseEffectHandler.getFortuneBonus();
            return original + bonus;
        }

        return original;
    }

    /**
     * TARGET: Looting (Mob Drops)
     * This method is called whenever a mob dies to calculate drops.
     */
    @ModifyReturnValue(method = "getEnchantmentLevel", at = @At("RETURN"))
    private static int modifyMobLootingLevel(int original,Holder<Enchantment> enchantment, LivingEntity entity) {
        if (entity instanceof Player player) {

            if (enchantment.is(Enchantments.LOOTING)){
                int bonus = CardCaseEffectHandler.getLootingBonus();
                if (bonus > 0) {
                    return original + bonus;
                }
            }
        }
        return original;
    }

}