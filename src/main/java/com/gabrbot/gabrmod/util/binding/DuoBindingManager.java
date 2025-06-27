package com.gabrbot.gabrmod.util.binding;

import com.gabrbot.gabrmod.block.entity.QtiteAlterBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class DuoBindingManager implements IBindingManager {
    private List<Player> bindingPlayers;

    @Override
    public void init(QtiteAlterBlockEntity qabe) {
        bindingPlayers = qabe.bindingPlayers;
    }

    @Override
    public void bind(QtiteAlterBlockEntity qabe) {
        List<Player> stillBindingPlayers = getPlayersStillBinding(qabe);
        BindingUtils.setBoundPlayers(qabe.getRenderStack(), stillBindingPlayers.get(0), stillBindingPlayers.get(1));
    }

    @Override
    public boolean stillBinding(QtiteAlterBlockEntity qabe) {
        return getPlayersStillBinding(qabe).size() >= 2;
    }

    private List<Player> getPlayersStillBinding(QtiteAlterBlockEntity qabe) {
        return bindingPlayers.stream().filter(p -> qabe.bindingPlayers.contains(p)).toList();
    }
}
