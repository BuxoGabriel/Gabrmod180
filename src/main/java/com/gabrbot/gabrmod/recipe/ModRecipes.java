package com.gabrbot.gabrmod.recipe;

import com.gabrbot.gabrmod.Gabrmod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Gabrmod.MODID);

    public static final RegistryObject<RecipeSerializer<BindingRecipe>> BINDING_RECIPE_SERIALIZER =
            SERIALIZERS.register("qtite_binding", () -> BindingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
