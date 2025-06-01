package com.gabrbot.gabrmod.block.custom;

import com.gabrbot.gabrmod.block.entity.ModBlockEntities;
import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;
import com.gabrbot.gabrmod.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QtiteAlterBlock extends BaseEntityBlock {
    public QtiteAlterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.gabrmod.qtite_alter_tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof QtiteAlterBlockEntity qaBlockEntity) {
                qaBlockEntity.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide()) {
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        BlockEntity e = pLevel.getBlockEntity(pPos);
        if(e instanceof QtiteAlterBlockEntity qaBlockEntity) {
            qaBlockEntity.resetProgress();
            qaBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                ItemStack oldItem = iItemHandler.extractItem(0, 1, false);
                ItemStack handItems = pPlayer.getItemInHand(pHand);
                if(!handItems.equals(ItemStack.EMPTY)) {
                    this.setBindingItem(iItemHandler, handItems);
                    ItemHandlerHelper.giveItemToPlayer(pPlayer, oldItem, EquipmentSlot.MAINHAND.getIndex());
                } else {
                    pPlayer.setItemInHand(pHand, oldItem);
                }
            });
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    private boolean setBindingItem(IItemHandler itemHandler, ItemStack stack) {
        if(stack.is(ModTags.Items.BINDABLE)) {
            itemHandler.insertItem(0, stack.split(1), false);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new QtiteAlterBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.QTITE_ALTER_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}
