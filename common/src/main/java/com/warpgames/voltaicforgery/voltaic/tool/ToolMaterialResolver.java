package com.warpgames.voltaicforgery.voltaic.tool;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public final class ToolMaterialResolver {

    private ToolMaterialResolver() {}

    public static ToolMaterialStat resolve(HolderLookup.Provider registries, String materialId) {
        return resolveOptional(registries, materialId).orElse(ToolMaterialStat.FALLBACK);
    }

    public static Optional<ToolMaterialStat> resolveOptional(HolderLookup.Provider registries, String materialId) {
        if (registries == null) return Optional.empty();

        Identifier id = VoltaicRegistries.materialKey(materialId).identifier();
        return registries.lookup(VoltaicRegistries.TOOL_MATERIALS)
                .flatMap(registry -> registry.get(VoltaicRegistries.materialKey(id)))
                .map(reference -> reference.value());
    }
}
