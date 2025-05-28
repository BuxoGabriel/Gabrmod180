package com.gabrbot.gabrmod.datagen;

import com.gabrbot.gabrmod.block.ModBlocks;
import com.gabrbot.gabrmod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.QTITE_BLOCK.get())
                .pattern("QQQ")
                .pattern("QQQ")
                .pattern("QQQ")
                .define('Q', ModItems.QTITE.get())
                .unlockedBy(getHasName(ModItems.QTITE.get()), has(ModItems.QTITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.QTITE_WAND.get())
                .pattern("  Q")
                .pattern(" Q ")
                .pattern("S  ")
                .define('Q', ModItems.QTITE.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.QTITE.get()), has(ModItems.QTITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.QTITE_ALTER.get())
                .pattern("GBG")
                .pattern("SQS")
                .pattern("GIG")
                .define('Q', ModItems.QTITE.get())
                .define('B', Items.BOWL)
                .define('G', Items.GOLD_INGOT)
                .define('S', Items.STONE_BRICKS)
                .define('I', Items.IRON_BLOCK)
                .unlockedBy(getHasName(ModItems.QTITE.get()), has(ModItems.QTITE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.QTITE.get(), 9)
                .requires(ModBlocks.QTITE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.QTITE_BLOCK.get()), has(ModBlocks.QTITE_BLOCK.get()))
                .save(pWriter);
    }
}
