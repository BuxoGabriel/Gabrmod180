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
        Tag bound1Tag = tag.get("bound0");
        Tag bound2Tag = tag.get("bound1");
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
        Tag bound1Tag = tag.get("bound0");
        Tag bound2Tag = tag.get("bound1");
        if (bound1Tag instanceof StringTag bound1 && bound2Tag instanceof StringTag bound2) {
            return Optional.of(new Tuple(bound1.getAsString(), bound2.getAsString()));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Tuple<String, String>> getBoundPlayersNames(ItemStack itemStack) {
        if(!itemStack.hasTag()) return Optional.empty();
        CompoundTag tag = itemStack.getOrCreateTag();
        Tag bound1Tag = tag.get("bound0name");
        Tag bound2Tag = tag.get("bound1name");
        if (bound1Tag instanceof StringTag bound1 && bound2Tag instanceof StringTag bound2) {
            return Optional.of(new Tuple(bound1.getAsString(), bound2.getAsString()));
        } else {
            return Optional.empty();
        }
    }

    public static void setBoundPlayers(ItemStack itemStack, Player bound1, Player bound2) {
        CompoundTag itemTags = itemStack.getOrCreateTag();

        itemTags.put("bound0", StringTag.valueOf(bound1.getStringUUID()));
        itemTags.put("bound1", StringTag.valueOf(bound2.getStringUUID()));
        itemTags.put("bound0name", StringTag.valueOf(bound1.getDisplayName().getString()));
        itemTags.put("bound1name", StringTag.valueOf(bound2.getDisplayName().getString()));
    }

    public static void setBoundGroupPlayers(ItemStack itemStack, List<Player> bindingPlayers) {
        CompoundTag itemTags = itemStack.getOrCreateTag();
        for(int i = 0; i < bindingPlayers.size(); i++) {
            itemTags.put("bound_group" + i,
                    StringTag.valueOf(bindingPlayers.get(i).getStringUUID()));
            itemTags.put("bound_group" + i + "name",
                    StringTag.valueOf(bindingPlayers.get(i).getDisplayName().getString()));
        }
    }

    public static IBindingManager getBindingManager(QtiteAlterBlockEntity qabe) {
        if(SimpleBindingManager.simpleBindingMap.containsKey(qabe.getRenderStack().getItem())) return new SimpleBindingManager();
        else if(qabe.getRenderStack().is(ModTags.Items.BINDABLE)) return new DuoBindingManager();
        else return null;
    }

    public static boolean isBindable(ItemStack stack) {
        return SimpleBindingManager.simpleBindingMap.containsKey(stack.getItem()) || stack.is(ModTags.Items.BINDABLE);
    }
}