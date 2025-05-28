package com.gabrbot.gabrmod.datagen;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Gabrmod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.QTITE_BLOCK);
        blockBottomSideTopWithItem(ModBlocks.QTITE_ALTER);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void blockBottomSideTopWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().cubeBottomTop(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                        blockSideTexture(blockRegistryObject, "side"),
                        blockSideTexture(blockRegistryObject, "bottom"),
                        blockSideTexture(blockRegistryObject, "top")));
    }

    private ResourceLocation blockSideTexture(RegistryObject<Block> blockRegistryObject, String side) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get());
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + side);
    }
}
