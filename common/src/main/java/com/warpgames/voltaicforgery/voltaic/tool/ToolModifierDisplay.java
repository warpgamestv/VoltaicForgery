package com.warpgames.voltaicforgery.voltaic.tool;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;

import java.util.List;

public record ToolModifierDisplay(ToolModifierEntry entry) {

    public static List<ToolModifierDisplay> all(HolderLookup.Provider registries) {
        if (registries == null) {
            return List.of();
        }
        return registries.lookup(VoltaicRegistries.TOOL_MODIFIERS)
                .stream()
                .flatMap(HolderLookup.RegistryLookup::listElements)
                .map(Holder::value)
                .map(ToolModifierDisplay::new)
                .toList();
    }
}
