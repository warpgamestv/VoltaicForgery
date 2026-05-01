package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class NeoForgeVoltaicDatapackRegistries {

    private NeoForgeVoltaicDatapackRegistries() {}

    static void attach(IEventBus bus) {
        bus.addListener(NeoForgeVoltaicDatapackRegistries::registerDatapackRegistries);
    }

    private static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VoltaicRegistries.TOOL_MATERIALS, ToolMaterialStat.CODEC, ToolMaterialStat.CODEC);
        event.dataPackRegistry(VoltaicRegistries.TOOL_MODIFIERS, ToolModifierEntry.CODEC, ToolModifierEntry.CODEC);
    }
}
