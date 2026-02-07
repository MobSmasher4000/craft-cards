package org.mob.craftcards.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class AddItemModifier extends LootModifier {

    // Codec to read the JSON file
    public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).and(
                    inst.group(
                            ItemStack.CODEC.fieldOf("item").forGetter(m -> m.item),
                            com.mojang.serialization.Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance)
                    )
            ).apply(inst, AddItemModifier::new)));

    private final ItemStack item;
    private final float chance;

    /**
     * @param conditionsIn Standard Loot Conditions (e.g. is it the fishing table?)
     * @param item         The item to add
     * @param chance       The chance (0.0 to 1.0) to add it
     */
    public AddItemModifier(LootItemCondition[] conditionsIn, ItemStack item, float chance) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Check random chance (e.g. 0.25 = 25% chance)
        if (context.getRandom().nextFloat() < this.chance) {
            // Add a copy of the item to not modify the source stack
            generatedLoot.add(this.item.copy());
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}