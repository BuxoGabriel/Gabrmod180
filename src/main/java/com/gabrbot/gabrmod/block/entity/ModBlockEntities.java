package com.gabrbot.gabrmod.block.entity;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Gabrmod.MODID);

    public static final RegistryObject<BlockEntityType<QtiteAlterBlockEntity>> QTITE_ALTER_BE =
            BLOCK_ENTITIES.register("qtite_alter_be", () ->
                    BlockEntityType.Builder.of(QtiteAlterBlockEntity::new,
                            ModBlocks.QTITE_ALTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
