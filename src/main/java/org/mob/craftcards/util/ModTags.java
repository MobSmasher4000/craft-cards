package org.mob.craftcards.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.mob.craftcards.CraftCards;

import java.util.Arrays;

public class ModTags {

    public static final TagKey<Item> CARD_ITEMS = tagmaker("card_items");
    public static final TagKey<Item> LOOT_CARD_ITEMS = tagmaker("loot_card_items");

    public static final TagKey<Item> GLOBAL_EXEMPT = tagmaker("global_exempt");
    public static final TagKey<Item> FLIGHT = tagmaker("flight");
    public static final TagKey<Item> ARMOR = tagmaker("armor");
    public static final TagKey<Item> ARMOR_TOUGHNESS = tagmaker("armor_toughness");
    public static final TagKey<Item> ATTACK_SPEED = tagmaker("attack_speed");
    public static final TagKey<Item> BLOCK_INTERACTION_RANGE = tagmaker("block_interaction_range");
    public static final TagKey<Item> CAPTURE_RATE = tagmaker("capture_rate");
    public static final TagKey<Item> DAMAGE = tagmaker("damage");
    public static final TagKey<Item> ENTITY_INTERACTION_RANGE = tagmaker("entity_interaction_range");
    public static final TagKey<Item> FEATHER_FALLING = tagmaker("feather_falling");
    public static final TagKey<Item> FIRE_RESISTANCE = tagmaker("fire_resistance");
    public static final TagKey<Item> FORTUNE = tagmaker("fortune");
    public static final TagKey<Item> HEALTH_BOOST = tagmaker("health_boost");
    public static final TagKey<Item> JUMP_BOOST = tagmaker("jump_boost");
    public static final TagKey<Item> LOOTING = tagmaker("looting");
    public static final TagKey<Item> LUCK_BOOST = tagmaker("luck_boost");
    public static final TagKey<Item> MINING_SPEED = tagmaker("mining_speed");
    public static final TagKey<Item> REGENERATION = tagmaker("regeneration");
    public static final TagKey<Item> SHINY_RATE = tagmaker("shiny_rate");
    public static final TagKey<Item> SIZE_DOWN = tagmaker("size_down");
    public static final TagKey<Item> SIZE_UP = tagmaker("size_up");
    public static final TagKey<Item> SNEAK_SPEED = tagmaker("sneak_speed");
    public static final TagKey<Item> SPEED_BOOST = tagmaker("speed_boost");
    public static final TagKey<Item> STEP_HEIGHT = tagmaker("step_height");
    public static final TagKey<Item> UNDERWATER_MINING_SPEED = tagmaker("underwater_mining_speed");
    public static final TagKey<Item> WATER_BREATHING = tagmaker("water_breathing");

    public static final TagKey<Item>[] CARD_CASE_SLOT_TAGS = new TagKey[] {
            // 1. Luck Card
            LUCK_BOOST,
            // 2. Mining Speed Card
            MINING_SPEED,
            // 3. Underwater Mining Speed Card
            UNDERWATER_MINING_SPEED,
            // 4. Health Boost Card
            HEALTH_BOOST,
            // 5. Regeneration Card
            REGENERATION,
            // 6. Water Breathing Card
            WATER_BREATHING,
            // 7. Size Down Card
            SIZE_DOWN,
            // 8. Size Up Card
            SIZE_UP,
            // 9. Sharpness Card (Mapped to DAMAGE)
            DAMAGE,
            // 10. Attack Speed Card
            ATTACK_SPEED,
            // 11. Sneak Speed Card
            SNEAK_SPEED,
            // 12. Step Height
            STEP_HEIGHT,
            // 13. Block Reach Distance Card (Mapped to BLOCK_INTERACTION_RANGE)
            BLOCK_INTERACTION_RANGE,
            // 14. Entity Reach Distance Card (Mapped to ENTITY_INTERACTION_RANGE)
            ENTITY_INTERACTION_RANGE,
            // 15. Movement Speed Card (Mapped to SPEED_BOOST)
            SPEED_BOOST,
            // 16. Jump Boost Card
            JUMP_BOOST,
            // 17. Feather Falling Card
            FEATHER_FALLING,
            // 18. Armor Toughness Card
            ARMOR_TOUGHNESS,
            // 19. Armor Card
            ARMOR,
            // 20. Fire Resistance Card
            FIRE_RESISTANCE,
            // 21. Shiny Rate Card
            SHINY_RATE,
            // 22. Capture Rate Card
            CAPTURE_RATE,
            // 23. Fortune
            FORTUNE,
            // 24. Looting
            LOOTING,
            // 25. Flight
            FLIGHT
    };

    private static TagKey<Item> tagmaker(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CraftCards.MOD_ID, name));
    }
}