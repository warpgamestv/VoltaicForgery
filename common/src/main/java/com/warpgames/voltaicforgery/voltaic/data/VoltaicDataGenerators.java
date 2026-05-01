package com.warpgames.voltaicforgery.voltaic.data;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

public final class VoltaicDataGenerators {

    private VoltaicDataGenerators() {}

    public static void gatherData(DataGenerator generator, PackOutput output) {
        DataGenerator.PackGenerator pack = generator.getVanillaPack(true);
        pack.addProvider(VoltaicItemModelProvider::new);
        pack.addProvider(VoltaicBlockStateModelProvider::new);
        pack.addProvider(VoltaicLanguageProvider::new);
        pack.addProvider(VoltaicCastingRecipeProvider::new);
        pack.addProvider(com.warpgames.voltaicforgery.voltaic.data.VoltaicAssemblyRecipeProvider::new);
    }

    public static RegistrySetBuilder registrySetBuilder() {
        return new RegistrySetBuilder()
                .add(VoltaicRegistries.TOOL_MATERIALS, VoltaicRegistries::bootstrapToolMaterials)
                .add(VoltaicRegistries.TOOL_MODIFIERS, VoltaicRegistries::bootstrapToolModifiers);
    }
}
