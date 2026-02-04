package org.mob.craftcards.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.mob.craftcards.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates all card crafting recipes (upcraft and downcraft) as JSON files.
 */
public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Register recipes from T0 up to T5-> T6 (or until the second-to-last tier)
        makeRecipe(ModItems.ARMOR, exporter);
        makeRecipe(ModItems.ARMOR_TOUGHNESS, exporter);
        makeRecipe(ModItems.HEALTH_BOOST, exporter);
        makeRecipe(ModItems.REGENERATION, exporter);
        makeRecipe(ModItems.DAMAGE, exporter);
        makeRecipe(ModItems.ATTACK_SPEED, exporter);
        makeRecipe(ModItems.SIZE_UP, exporter);
        makeRecipe(ModItems.SIZE_DOWN, exporter);
        makeRecipe(ModItems.MINING_SPEED, exporter);
        makeRecipe(ModItems.UNDERWATER_MINING_SPEED, exporter);
        makeRecipe(ModItems.BLOCK_INTERACTION_RANGE, exporter);
        makeRecipe(ModItems.ENTITY_INTERACTION_RANGE, exporter);
        makeRecipe(ModItems.LUCK_BOOST, exporter);
        makeRecipe(ModItems.STEP_HEIGHT, exporter);
        makeRecipe(ModItems.JUMP_BOOST, exporter);
        makeRecipe(ModItems.FEATHER_FALLING, exporter);
        makeRecipe(ModItems.SPEED_BOOST, exporter);
        makeRecipe(ModItems.SNEAK_SPEED, exporter);
        makeRecipe(ModItems.SHINY_RATE, exporter);
        makeRecipe(ModItems.CAPTURE_RATE, exporter);
        makeRecipe(ModItems.FORTUNE, exporter);
        makeRecipe(ModItems.LOOTING, exporter);
    }

    private void makeRecipe(List<Item> list, RecipeExporter exporter){
        for (int i = 0; i < list.size() - 1; i++) {
            Item lowerTierItem = list.get(i);
            Item higherTierItem = list.get(i+1);

            // Upcraft: 4x T(n) -> 1x T(n+1)
            generateUpcraftRecipe(exporter, lowerTierItem, higherTierItem);

            // Downcraft: 1x T(n+1) -> 4x T(n)
            generateDowncraftRecipe(exporter, higherTierItem, lowerTierItem);
        }
    }

    /**
     * Creates and registers a shaped recipe where 4 lower-tier items create 1 higher-tier item (2x2 pattern).
     */
    private void generateUpcraftRecipe(RecipeExporter exporter, Item lowerTierItem, Item higherTierItem) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, higherTierItem, 1)
                .pattern("##")
                .pattern("##")
                .input('#', lowerTierItem)
                .criterion(hasItem(lowerTierItem), conditionsFromItem(lowerTierItem))
                .group("card_upcraft")
                .offerTo(exporter, Identifier.of(getRecipeId(higherTierItem).toString() + "_upcraft"));
    }

    /**
     * Creates and registers a shapeless recipe where 1 higher-tier item yields 4 lower-tier items.
     */
    private void generateDowncraftRecipe(RecipeExporter exporter, Item higherTierItem, Item lowerTierItem) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, lowerTierItem, 4)
                .input(higherTierItem)
                .criterion(hasItem(higherTierItem), conditionsFromItem(higherTierItem))
                .group("card_downcraft")
                .offerTo(exporter, Identifier.of(getRecipeId(lowerTierItem).toString() + "_downcraft"));
    }

    // Helper method to get a recipe ID compatible with Datagen syntax
    private Identifier getRecipeId(Item item) {
        return Registries.ITEM.getId(item);
    }
}