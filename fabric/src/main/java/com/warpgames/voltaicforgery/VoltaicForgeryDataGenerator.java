package com.warpgames.voltaicforgery;

import com.warpgames.voltaicforgery.voltaic.data.VoltaicBlockStateModelProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicCastingRecipeProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicItemModelProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicLanguageProvider;
import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;

public class VoltaicForgeryDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider((DataProvider.Factory<VoltaicCastingRecipeProvider>) VoltaicCastingRecipeProvider::new);
        pack.addProvider((DataProvider.Factory<com.warpgames.voltaicforgery.voltaic.data.VoltaicAssemblyRecipeProvider>) com.warpgames.voltaicforgery.voltaic.data.VoltaicAssemblyRecipeProvider::new);
        pack.addProvider((DataProvider.Factory<VoltaicBlockStateModelProvider>) VoltaicBlockStateModelProvider::new);
        pack.addProvider((DataProvider.Factory<VoltaicItemModelProvider>) VoltaicItemModelProvider::new);
        pack.addProvider((DataProvider.Factory<VoltaicLanguageProvider>) VoltaicLanguageProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(VoltaicRegistries.TOOL_MATERIALS, VoltaicRegistries::bootstrapToolMaterials);
        registryBuilder.add(VoltaicRegistries.TOOL_MODIFIERS, VoltaicRegistries::bootstrapToolModifiers);
    }
}
