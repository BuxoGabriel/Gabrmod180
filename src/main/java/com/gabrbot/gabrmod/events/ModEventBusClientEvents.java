package com.gabrbot.gabrmod.events;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.block.entity.ModBlockEntities;
import com.gabrbot.gabrmod.block.entity.renderer.QtiteAlterBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Gabrmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.QTITE_ALTER_BE.get(), QtiteAlterBlockEntityRenderer::new);
    }
}
