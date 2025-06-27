package com.gabrbot.gabrmod.util.binding;

import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBindingManager {
    public void init(QtiteAlterBlockEntity qabe);
    public void bind(QtiteAlterBlockEntity qabe);
    public boolean stillBinding(QtiteAlterBlockEntity qabe);
}