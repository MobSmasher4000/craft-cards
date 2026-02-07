package org.mob.craftcards.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class AddTagModifier extends LootModifier {

    public static final Supplier<MapCodec<AddTagModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(
                    inst.group(
                            TagKey.hashedCodec(Registries.ITEM).fieldOf("tag").forGetter(m -> m.tag),
                            com.mojang.serialization.Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance)
                    )
            ).apply(inst, AddTagModifier::new)));

    private final TagKey<Item> tag;
    private final float chance;

    public AddTagModifier(LootItemCondition[] conditionsIn, TagKey<Item> tag, float chance) {
        super(conditionsIn);
        this.tag = tag;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Check Chance
        if (context.getRandom().nextFloat() >= this.chance) {
            return generatedLoot;
        }

        // Find all items in the tag
        Optional<List<Holder<Item>>> items = BuiltInRegistries.ITEM.getTag(this.tag).map(holders -> holders.stream().toList());

        if (items.isPresent() && !items.get().isEmpty()) {
            List<Holder<Item>> validItems = items.get();

            // Pick one random item
            Holder<Item> randomItem = validItems.get(context.getRandom().nextInt(validItems.size()));

            // Add to loot
            generatedLoot.add(new ItemStack(randomItem));
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}