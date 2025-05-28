package com.gabrbot.gabrmod.item.custom;

import com.gabrbot.gabrmod.util.BindingUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class QtiteWandItem extends Item {
    public QtiteWandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(
            @NotNull Level pLevel,
            @NotNull Player pPlayer,
            @NotNull InteractionHand pUsedHand
    ) {
        // Get Item in player hand
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide() && pPlayer instanceof ServerPlayer player && pLevel instanceof ServerLevel level) {
            Optional<Tuple<ServerPlayer, ServerPlayer>> boundPlayers = BindingUtils.getBoundPlayers(itemStack, level);
            if(boundPlayers.isEmpty()) {
                player.sendSystemMessage(Component.literal("Qtite Wand is not bound!"));
                return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
            }
            ServerPlayer boundPlayer1 = boundPlayers.get().getA();
            ServerPlayer boundPlayer2 = boundPlayers.get().getB();
            ServerPlayer target;
            if (player.is(boundPlayer1) && isValidTeleportTarget(boundPlayer2)) {
                target = boundPlayer2;
            } else if (player.is(boundPlayer2) && isValidTeleportTarget(boundPlayer1)) {
                target = boundPlayer1;
            } else {
                // Either player is not bound to wand or target is invalid
                player.sendSystemMessage(Component.literal("You are not bound to this item or teleport target is not valid!"));
                return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
            }
            // Player is bound to wand and will teleport to target
            player.sendSystemMessage(Component.literal("Teleporting to " + target.getDisplayName().getString()));
            teleport(player, target);
        }
        pPlayer.getItemInHand(pUsedHand).hurtAndBreak(1, pPlayer,
                p -> p.broadcastBreakEvent(p.getUsedItemHand()));
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        BindingUtils.setBoundPlayers(item, player, player);
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack pStack,
            @Nullable Level pLevel,
            @NotNull List<Component> pTooltipComponents,
            @NotNull TooltipFlag pIsAdvanced
    ) {
        Optional<Tuple<String, String>> boundPlayers = BindingUtils.getBoundPlayersNames(pStack);
        if(boundPlayers.isEmpty()) pTooltipComponents.add(Component.translatable("tooltip.gabrmod.qtite_wand_unbound_tooltip"));
        else pTooltipComponents.add(Component.literal("Bound to " + boundPlayers.get().getA()+ " and " + boundPlayers.get().getB()));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    /// Foil
    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return !BindingUtils.getBoundPlayersUUIDs(pStack).isEmpty();
    }

    private boolean isValidTeleportTarget(Player target) {
        return target != null && !target.isSleeping();
    }

    private void teleport(ServerPlayer player, Entity target) {
        if (player.connection.isAcceptingMessages() && !player.isSleeping()) {
            if (player.isPassenger()) {
                player.dismountTo(target.getX(), target.getY(), target.getZ());
            } else {
                player.teleportTo(target.getX(), target.getY(), target.getZ());
            }
            player.resetFallDistance();
        }
    }
}
