package com.gabrbot.gabrmod.util.binding;

import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;

public interface IBindingManager {
    public void bind(QtiteAlterBlockEntity qabe);
    public boolean stillBinding(QtiteAlterBlockEntity qabe);
}