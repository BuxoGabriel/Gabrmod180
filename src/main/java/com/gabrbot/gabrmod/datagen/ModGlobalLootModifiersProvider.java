package com.gabrbot.gabrmod.datagen;

import com.gabrbot.gabrmod.Gabrmod;
import com.gabrbot.gabrmod.item.ModItems;
import com.gabrbot.gabrmod.loot.AddItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, Gabrmod.MODID);
    }

    @Override
    protected void start() {
        add("qtite_from_ruined_portal", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/ruined_portal")).build(),
                LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.7F, 2.0F).build()
        }, ModItems.QTITE.get()));

        add("qtite_from_stronghold_crossing", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_crossing")).build(),
        }, ModItems.QTITE.get()));

        add("qtite_from_stronghold_corridor", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/stronghold_corridor")).build(),
                LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.7F, 2.0F).build()
        }, ModItems.QTITE.get()));

        add("qtite_in_stage_1", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/stage_1")).build(),
                LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.7F, 2.0F).build()
        }, ModItems.QTITE.get()));

        add("qtite_in_stage_2", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/stage_2")).build(),
        }, ModItems.QTITE.get()));
    }
}
