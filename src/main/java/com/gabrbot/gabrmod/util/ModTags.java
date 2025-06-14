package com.gabrbot.gabrmod.util;

import com.gabrbot.gabrmod.Gabrmod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Gabrmod.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> BINDABLE = tag("bindable");

        public static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Gabrmod.MODID, name));
        }
    }
}
