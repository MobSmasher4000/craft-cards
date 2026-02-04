package org.mob.craftcards.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.mob.craftcards.helper.Tier;
import org.mob.craftcards.util.ModTags;

import java.util.List;

public class TieredCardItem extends Item {

    public TieredCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack,
                              TooltipContext context,
                              List<Text> tooltip,
                              TooltipType type) {
        // Figure out which tier this item is
        Tier tier = Tier.fromItem(stack.getItem());

        if (stack.isIn(ModTags.REGENERATION)){
            return;
        }
        if (stack.isIn(ModTags.LOOTING)){
            return;
        }
        if (stack.isIn(ModTags.FORTUNE)){
            return;
        }

        // 0.02f -> 2, 1.50f -> 150
        int percent = Math.round(tier.getBonus() * 100.0f);

        // Get registry path, e.g. "armor_tier3" -> "armor"
        String path = Registries.ITEM.getId(stack.getItem()).getPath();
        String statId = path;
        int idx = path.indexOf("_tier");
        if (idx != -1) {
            statId = path.substring(0, idx); // armor, fortune, speed_boost, etc.
        }

        String statName = formatStatName(statId); // "Armor", "Speed Boost", ...

        // e.g. "+20% Armor"
        tooltip.add(
                Text.literal("+" + percent + "% " + statName)
                        .formatted(Formatting.AQUA)
        );
    }

    private String formatStatName(String id) {
        String[] parts = id.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            if (i > 0) sb.append(' ');
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            if (parts[i].length() > 1) {
                sb.append(parts[i].substring(1));
            }
        }
        return sb.toString();
    }
}
