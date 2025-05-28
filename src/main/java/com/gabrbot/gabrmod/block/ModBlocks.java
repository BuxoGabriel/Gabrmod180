package com.gabrbot.gabrmod.block;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Gabrmod.MODID);

    public static final RegistryObject<Block> QTITE_BLOCK = registerBlock("qtite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.AMETHYST)));

    public static final RegistryObject<Block> QTITE_ALTER = registerBlock("qtite_alter",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE)));

    public static <T extends Block> RegistryObject<Block> registerBlock(String name, Supplier<T> block) {
        RegistryObject<Block> blockRegister = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(blockRegister.get(), new Item.Properties()));
        return blockRegister;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
