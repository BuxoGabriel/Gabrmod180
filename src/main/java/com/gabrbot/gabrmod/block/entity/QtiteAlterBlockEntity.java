package com.gabrbot.gabrmod.block.entity;

import com.gabrbot.gabrmod.util.BindingUtils;
import com.gabrbot.gabrmod.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class QtiteAlterBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private static int MAX_PROGRESS = 40;
    private static double PLAYER_SEARCH_RANGE = 2.0;

    private static float RENDER_ROTATION_SPEED = 1.0F;
    private static float CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP = 4.0F;


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Stored NBT state
    private int progress = 0;

    private String binding1;
    private String binding2;

    // Unstored state
    private float rotation;

    public QtiteAlterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.QTITE_ALTER_BE.get(),pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    public ItemStack getRenderStack() {
        return itemHandler.getStackInSlot(0);
    }

    public float getRenderRotation() {
        rotation += 0.5F * RENDER_ROTATION_SPEED * (progress / MAX_PROGRESS * CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP + 1.0);
        if(rotation >= 360.0F) rotation -= 360.0F;
        return rotation;
    }

    public float getRenderHeight() {
        return progress / MAX_PROGRESS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("qtite_alter.progress", progress);
        if(binding1 != null) pTag.putString("qtite_alter.binding1", binding1);
        else pTag.putString("qtite_alter.binding1", "null");
        if(binding2 != null) pTag.putString("qtite_alter.binding2", binding2);
        else pTag.putString("qtite_alter.binding2", "null");
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("qtite_alter.progress");
        binding1 = pTag.getString("qtite_alter.binding1");
        binding2 = pTag.getString("qtite_alter.binding2");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        List<Player> nearbyShiftingPlayers = getNearbyShiftingPlayers(pLevel, pPos);
        if(hasRecipe() && nearbyShiftingPlayers.size() >= 2) {
            if(progress == 0) {
                // Progress has reset so any two players can bind item
                binding1 = nearbyShiftingPlayers.get(0).getStringUUID();
                binding2 = nearbyShiftingPlayers.get(1).getStringUUID();
            }
            List<Player> boundPlayers = nearbyShiftingPlayers.stream()
                    .filter(p -> p.getStringUUID() == binding1 || p.getStringUUID() == binding2)
                    .collect(Collectors.toList());
            if (boundPlayers.size() == 2) {
                progress++;
                if (progress >= MAX_PROGRESS) {
                    bindItem(boundPlayers.get(0), boundPlayers.get(1));
                    progress = 0;
                    if(!pLevel.isClientSide()) {
                        pLevel.addParticle(ParticleTypes.PORTAL, pPos.getCenter().x, pPos.getCenter().y + 1.5, pPos.getCenter().z + 0.5, 0.0, 0.0, 0.0);
                        pLevel.playSound((Player) null, pPos.getCenter().x, pPos.getCenter().y + 0.5, pPos.getCenter().z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else {
                decrementProgress();
            }
        } else {
                decrementProgress();
        }
        setChanged(pLevel, pPos, pState);
    }

    private void decrementProgress() {
        progress = Math.max(progress - 1, 0);
    }

    private List<Player> getNearbyShiftingPlayers(Level pLevel, BlockPos pPos) {
        return pLevel.players().stream().filter(p -> p.distanceToSqr(pPos.getCenter()) < PLAYER_SEARCH_RANGE * PLAYER_SEARCH_RANGE && p.isCrouching()).collect(Collectors.toList());
    }

    private void bindItem(Player bound1, Player bound2) {
        ItemStack itemStack = this.itemHandler.getStackInSlot(0);
        BindingUtils.setBoundPlayers(itemStack, bound1, bound2);
    }

    private boolean hasRecipe() {
        ItemStack itemStack = this.itemHandler.getStackInSlot(0);
        return itemStack.is(ModTags.Items.BINDABLE) && BindingUtils.getBoundPlayersUUIDs(itemStack).isEmpty();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
