package com.gabrbot.gabrmod.util.binding;

import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;
import com.gabrbot.gabrmod.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;


public class SimpleBindingManager implements IBindingManager {
    static Map<Item, Item> simpleBindingMap = Map.of(
            ModItems.QTITE.get(), ModItems.GROUPITE.get(),
            ModItems.GROUPITE.get(), ModItems.QTITE.get()
    );
    @Override
    public void init(QtiteAlterBlockEntity qabe) {}

    @Override
    public void bind(QtiteAlterBlockEntity qabe) {
        Item item = qabe.getRenderStack().getItem();
        if(simpleBindingMap.containsKey(item)) {
            qabe.setInventory(new ItemStack(simpleBindingMap.get(item)));
        }
    }

    @Override
    public boolean stillBinding(QtiteAlterBlockEntity qabe) {
        return qabe.bindingPlayers.size() >= 1;
    }
}
