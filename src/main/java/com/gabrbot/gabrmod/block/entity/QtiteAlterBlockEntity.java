package com.gabrbot.gabrmod.block.entity;

import com.gabrbot.gabrmod.item.ModItems;
import com.gabrbot.gabrmod.util.binding.BindingUtils;
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
import java.util.Optional;

public class QtiteAlterBlockEntity extends BlockEntity {

    private enum BINDINGTYPE {
        SIMPLE,
        DUO,
        GROUP;

        @Override
        public String toString() {
            return switch(this) {
                case SIMPLE -> "simple";
                case DUO -> "duo";
                case GROUP -> "group";
            };
        }
    }
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
    private static int PLAYER_SEARCH_RANGE = 4;

    private static float RENDER_ROTATION_SPEED = 1.0F;
    private static float CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP = 4.0F;


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Stored NBT state
    private int progress = 0;

    private BINDINGTYPE bindingtype;

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
        rotation += 0.5F * RENDER_ROTATION_SPEED * ((float)progress / MAX_PROGRESS * CRAFTING_PROGRESS_RENDER_ROTATION_SPEEDUP + 1.0);
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
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("qtite_alter.progress", progress);
        pTag.putString("qtite_alter.binding1", Objects.requireNonNullElse(binding1, "null"));
        pTag.putString("qtite_alter.binding2", Objects.requireNonNullElse(binding2, "null"));
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
        if(progress == 0) {
            this.bindingtype = getBindingType(this.itemHandler.getStackInSlot(0));
        }
        if(hasRecipe() && nearbyShiftingPlayers.size() >= 2) {
            if(progress == 0) {
                // Progress has reset so any two players can bind item
                binding1 = nearbyShiftingPlayers.get(0).getStringUUID();
                binding2 = nearbyShiftingPlayers.get(1).getStringUUID();
            }
            List<Player> boundPlayers = nearbyShiftingPlayers.stream()
                    .filter(p -> p.getStringUUID().equals(binding1) || p.getStringUUID().equals(binding2))
                    .toList();
            if (boundPlayers.size() == 2) {
                progress++;
                if (progress >= MAX_PROGRESS) {
                    bindItem(boundPlayers.get(0), boundPlayers.get(1));
                    resetProgress();
                    if(!pLevel.isClientSide()) {
                        pLevel.addParticle(ParticleTypes.PORTAL, pPos.getCenter().x, pPos.getCenter().y + 1.5, pPos.getCenter().z + 0.5, 0.0, 0.0, 0.0);
                        pLevel.playSound( null, pPos.getCenter().x, pPos.getCenter().y + 0.5, pPos.getCenter().z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
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

    public void resetProgress() {
        progress = 0;
        binding1 = null;
        binding2 = null;
        bindingtype = null;
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

    private BINDINGTYPE getBindingType(ItemStack item) {
        if(item.is(ModTags.Items.BINDABLE)) {
            return BINDINGTYPE.DUO;
        } else if(item.is(ModItems.QTITE.get())){
            return BINDINGTYPE.SIMPLE;
        } else {
            return null;
        }
    }

    private void bindItem(List<Player> bindingPlayers) {
        ItemStack item = this.itemHandler.getStackInSlot(0);
        BINDINGTYPE bindingType = getBindingType(item);
        if(bindingType == null) return;
        switch(bindingType) {
            case SIMPLE -> this.itemHandler.insertItem(0, new ItemStack(ModItems.GROUPITE.get()), false);
            case DUO -> {
                bindItem(bindingPlayers.get(0), bindingPlayers.get(1));
            }
            case GROUP -> {
                // TODO
            }
        }
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
