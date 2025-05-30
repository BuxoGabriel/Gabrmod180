package com.gabrbot.gabrmod.item;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.item.custom.QtiteWandItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Gabrmod.MODID);

    public static final RegistryObject<Item> QTITE = ITEMS.register("qtite",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> QTITE_WAND = ITEMS.register("qtite_wand",
            () -> new QtiteWandItem(new Item.Properties().durability(30)));

    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
}
