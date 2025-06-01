package com.gabrbot.gabrmod.util.binding;

import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;
import com.gabrbot.gabrmod.item.ModItems;
import com.gabrbot.gabrmod.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class BindingUtils {
    public static Optional<Tuple<ServerPlayer, ServerPlayer>> getBoundPlayers(ItemStack itemStack, ServerLevel level) {
        if(!itemStack.hasTag()) return Optional.empty();
        CompoundTag tag = itemStack.getOrCreateTag();
        Tag bound1Tag = tag.get("bound1");
        Tag bound2Tag = tag.get("bound2");
        if (bound1Tag instanceof StringTag boundTag1 && bound2Tag instanceof StringTag boundTag2) {
            return level.getPlayers(p -> p.getStringUUID().equals(boundTag1.getAsString()))
                    .stream().findFirst()
                    .flatMap(b1 -> level.getPlayers(p -> p.getStringUUID().equals(boundTag2.getAsString()))
                            .stream().findFirst()
                            .map(b2 -> new Tuple<ServerPlayer, ServerPlayer>(b1, b2))
                    );
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Tuple<String, String>> getBoundPlayersUUIDs(ItemStack itemStack) {
        if(!itemStack.hasTag()) return Optional.empty();
        CompoundTag tag = itemStack.getOrCreateTag();
        Tag bound1Tag = tag.get("bound1");
        Tag bound2Tag = tag.get("bound2");
        if (bound1Tag instanceof StringTag bound1 && bound2Tag instanceof StringTag bound2) {
            return Optional.of(new Tuple(bound1.getAsString(), bound2.getAsString()));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Tuple<String, String>> getBoundPlayersNames(ItemStack itemStack) {
        if(!itemStack.hasTag()) return Optional.empty();
        CompoundTag tag = itemStack.getOrCreateTag();
        Tag bound1Tag = tag.get("bound1name");
        Tag bound2Tag = tag.get("bound2name");
        if (bound1Tag instanceof StringTag bound1 && bound2Tag instanceof StringTag bound2) {
            return Optional.of(new Tuple(bound1.getAsString(), bound2.getAsString()));
        } else {
            return Optional.empty();
        }
    }

    public static void setBoundPlayers(ItemStack itemStack, Player bound1, Player bound2) {
        CompoundTag itemTags = itemStack.getOrCreateTag();
        itemTags.put("bound1", StringTag.valueOf(bound1.getStringUUID()));
        itemTags.put("bound2", StringTag.valueOf(bound2.getStringUUID()));
        itemTags.put("bound1name", StringTag.valueOf(bound1.getDisplayName().getString()));
        itemTags.put("bound2name", StringTag.valueOf(bound2.getDisplayName().getString()));
    }

    public static Optional<IBindingManager> bindingManager(ItemStack itemStack) {
        if(itemStack.is(ModItems.QTITE.get())) return Optional.of(new SimpleBindingManager());
        else if(itemStack.is(ModTags.Items.BINDABLE)) return Optional.of(new DuoBindingManager());
        else return Optional.empty();
    }
}