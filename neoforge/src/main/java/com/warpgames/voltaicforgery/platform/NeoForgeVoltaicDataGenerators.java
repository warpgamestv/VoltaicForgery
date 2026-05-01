package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.data.VoltaicBlockStateModelProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicCastingRecipeProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicDataGenerators;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicItemModelProvider;
import com.warpgames.voltaicforgery.voltaic.data.VoltaicLanguageProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class NeoForgeVoltaicDataGenerators {

    private NeoForgeVoltaicDataGenerators() {}

    static void attach(IEventBus bus) {
        bus.addListener(NeoForgeVoltaicDataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(VoltaicDataGenerators.registrySetBuilder());
        event.createProvider(VoltaicCastingRecipeProvider::new);
        event.createProvider(com.warpgames.voltaicforgery.voltaic.data.VoltaicAssemblyRecipeProvider::new);
        event.createProvider(VoltaicBlockStateModelProvider::new);
        event.createProvider(VoltaicItemModelProvider::new);
        event.createProvider(VoltaicLanguageProvider::new);
    }
}
