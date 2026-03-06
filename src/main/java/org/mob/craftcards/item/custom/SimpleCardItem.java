package org.mob.craftcards.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SimpleCardItem extends Item {
    private static String Tooltip="";
    public SimpleCardItem(Settings settings, String tooltip) {
        super(settings);
        Tooltip = tooltip;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(Tooltip).formatted(Formatting.AQUA));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
