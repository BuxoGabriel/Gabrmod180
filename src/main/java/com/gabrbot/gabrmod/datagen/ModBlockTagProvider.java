package com.gabrbot.gabrmod.datagen;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Gabrmod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.QTITE_BLOCK.get())
                .add(ModBlocks.QTITE_ALTER.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.QTITE_BLOCK.get())
                .add(ModBlocks.QTITE_ALTER.get());
    }
}
