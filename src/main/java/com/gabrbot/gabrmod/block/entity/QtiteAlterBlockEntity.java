package com.gabrbot.gabrmod.block.entity;

import com.gabrbot.gabrmod.util.binding.BindingUtils;
import com.gabrbot.gabrmod.util.binding.IBindingManager;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

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
    private static int MAX_PROGRESS = 60;
    private static int PLAYER_SEARCH_RANGE = 4;

    private static float RENDER_ROTATION_SPEED = 0.5F;
    private static float CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP = 10.0F;


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Stored NBT state
    private int progress = 0;


    // Unstored state
    private IBindingManager bindingManager;
    public List<Player> bindingPlayers = List.of();
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
        float renderProgress = (float) progress / MAX_PROGRESS;
        // Rotation Animation curve: -4x^2 + 4x)
        rotation += RENDER_ROTATION_SPEED * (1.0F - 4.0F * (renderProgress * renderProgress - renderProgress) * CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP);
        if(rotation >= 360.0F) rotation -= 360.0F;
        return rotation;
    }

    public float getRenderHeight() {
        return (float) progress / MAX_PROGRESS;
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
        pTag.put("qtite_alter.inventory", itemHandler.serializeNBT());
        pTag.putInt("qtite_alter.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("qtite_alter.inventory"));
        progress = pTag.getInt("qtite_alter.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        int lastProgress = progress;
        if(pLevel.isClientSide()) return;
        bindingPlayers = getNearbyShiftingPlayers(pLevel, pPos);
        if(bindingManager == null) {
            resetProgress();
            bindingManager = BindingUtils.getBindingManager(this);
            if(bindingManager == null) return;
            else bindingManager.init(this);
        } else if(bindingManager.stillBinding(this)) {
            progress++;
            if(progress >= MAX_PROGRESS) {
                bindingManager.bind(this);
                resetProgress();
                pLevel.addParticle(ParticleTypes.PORTAL, pPos.getCenter().x, pPos.getCenter().y + 1.5, pPos.getCenter().z + 0.5, 0.0, 0.0, 0.0);
                pLevel.playSound( null, pPos.getCenter().x, pPos.getCenter().y + 0.5, pPos.getCenter().z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else {
            decrementProgress();
            if(progress <= 0)
                bindingManager.init(this);
        }
        if(progress != lastProgress) setChanged(pLevel, pPos, pState);
        pLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public void resetProgress() {
        progress = 0;
        rotation = 0;
        bindingPlayers = List.of();
        bindingManager = null;
    }

    private void decrementProgress() {
        progress = Math.max(progress - 1, 0);
    }

    private List<Player> getNearbyShiftingPlayers(Level pLevel, BlockPos pPos) {
        return pLevel.getEntities( null,
                new AABB(
                        pPos.offset(-PLAYER_SEARCH_RANGE, -PLAYER_SEARCH_RANGE, -PLAYER_SEARCH_RANGE),
                        pPos.offset(PLAYER_SEARCH_RANGE, PLAYER_SEARCH_RANGE, PLAYER_SEARCH_RANGE)
                )
        ).stream()
                .filter(p -> p instanceof Player player && player.isCrouching())
                .map(p -> (Player) p).toList();
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

    public void setInventory(ItemStack itemStack) {
        this.itemHandler.extractItem(0, 1, false);
        this.itemHandler.insertItem(0, itemStack, false);
    }
}
