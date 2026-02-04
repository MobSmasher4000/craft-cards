package org.mob.craftcards.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.mob.craftcards.item.ModItems;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // no blocks here
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        ModnameItemModelGenerator generator = new ModnameItemModelGenerator(itemModelGenerator);
        // --- Standard Item Registration ---
        itemModelGenerator.register(ModItems.CARD_CASE , Models.GENERATED);

        // Standard registration for non-tiered items
        itemModelGenerator.register(ModItems.FIRE_RESISTANCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_BREATHING, Models.GENERATED);

        for (var card : ModItems.ARMOR){
            generator.registerPrefixed(card, "armor/", Models.GENERATED);
        }

        for (var card : ModItems.ARMOR_TOUGHNESS){
            generator.registerPrefixed(card, "armor_toughness/", Models.GENERATED);
        }

        for (var card : ModItems.HEALTH_BOOST){
            generator.registerPrefixed(card, "health_boost/", Models.GENERATED);
        }

        for (var card : ModItems.REGENERATION){
            generator.registerPrefixed(card, "regeneration/", Models.GENERATED);
        }

        for (var card : ModItems.DAMAGE){
            generator.registerPrefixed(card, "damage/", Models.GENERATED);
        }

        for (var card : ModItems.ATTACK_SPEED){
            generator.registerPrefixed(card, "attack_speed/", Models.GENERATED);
        }

        for (var card : ModItems.SIZE_UP){
            generator.registerPrefixed(card, "size_up/", Models.GENERATED);
        }

        for (var card : ModItems.SIZE_DOWN){
            generator.registerPrefixed(card, "size_down/", Models.GENERATED);
        }

        for (var card : ModItems.MINING_SPEED){
            generator.registerPrefixed(card, "mining_speed/", Models.GENERATED);
        }

        for (var card : ModItems.UNDERWATER_MINING_SPEED){
            generator.registerPrefixed(card, "underwater_mining_speed/", Models.GENERATED);
        }

        for (var card : ModItems.BLOCK_INTERACTION_RANGE){
            generator.registerPrefixed(card, "block_interaction_range/", Models.GENERATED);
        }

        for (var card : ModItems.ENTITY_INTERACTION_RANGE){
            generator.registerPrefixed(card, "entity_interaction_range/", Models.GENERATED);
        }

        for (var card : ModItems.JUMP_BOOST){
            generator.registerPrefixed(card, "jump_boost/", Models.GENERATED);
        }

        for (var card : ModItems.LUCK_BOOST){
            generator.registerPrefixed(card, "luck_boost/", Models.GENERATED);
        }

        for (var card : ModItems.FEATHER_FALLING){
            generator.registerPrefixed(card, "feather_falling/", Models.GENERATED);
        }

        for (var card : ModItems.STEP_HEIGHT){
            generator.registerPrefixed(card, "step_height/", Models.GENERATED);
        }

        for (var card : ModItems.SNEAK_SPEED){
            generator.registerPrefixed(card, "sneak_speed/", Models.GENERATED);
        }

        for (var card : ModItems.SPEED_BOOST){
            generator.registerPrefixed(card, "speed_boost/", Models.GENERATED);
        }

        for (var card : ModItems.SHINY_RATE){
            generator.registerPrefixed(card, "shiny_rate/", Models.GENERATED);
        }

        for (var card : ModItems.CAPTURE_RATE){
            generator.registerPrefixed(card, "capture_rate/", Models.GENERATED);
        }

        for (var card : ModItems.LOOTING){
            generator.registerPrefixed(card, "looting/", Models.GENERATED);
        }

        for (var card : ModItems.FORTUNE){
            generator.registerPrefixed(card, "fortune/", Models.GENERATED);
        }

    }

    /**
     * @author rotgruengelb
     */
    private static class ModnameItemModelGenerator extends ItemModelGenerator {
        public ModnameItemModelGenerator(ItemModelGenerator itemModelGenerator) {
            super(itemModelGenerator.writer);
        }

        public void registerPrefixed(Item item, String prefix, Model model) {
            model.upload(getPreId(item, ""), TextureMap.layer0(getPreId(item, prefix)), this.writer);
        }

        public static Identifier getPreId(Item item, String prefix) {
            Identifier identifier = Registries.ITEM.getId(item);
            return identifier.withPath(path -> "item/" + prefix + path);
        }
    }

}