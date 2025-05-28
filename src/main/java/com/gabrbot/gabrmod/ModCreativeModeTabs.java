package com.gabrbot.gabrmod;

import com.gabrbot.gabrmod.block.ModBlocks;
import com.gabrbot.gabrmod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gabrmod.MODID);

    public static final RegistryObject<CreativeModeTab> GABRMOD_ITEMS_TAB = CREATIVE_MODE_TABS.register("gabrmod_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.QTITE.get()))
                    .title(Component.translatable("creativetab.gabrmod_items_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.QTITE.get());
                        pOutput.accept(ModItems.QTITE_WAND.get());
                        pOutput.accept(ModBlocks.QTITE_BLOCK.get());
                        pOutput.accept(ModBlocks.QTITE_ALTER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
