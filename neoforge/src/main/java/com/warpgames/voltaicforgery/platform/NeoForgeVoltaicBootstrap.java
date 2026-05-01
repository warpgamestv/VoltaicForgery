package com.warpgames.voltaicforgery.platform;

import net.neoforged.bus.api.IEventBus;

public final class NeoForgeVoltaicBootstrap {

    private NeoForgeVoltaicBootstrap() {}

    public static void init(IEventBus bus) {
        NeoForgeVoltaicDatapackRegistries.attach(bus);
        NeoForgeVoltaicFluidTypes.attach(bus);
        NeoForgeVoltaicRegistrar.attach(bus);
        NeoForgeVoltaicCapabilities.attach(bus);
        NeoForgeVoltaicClient.attach(bus);
        NeoForgeVoltaicDataGenerators.attach(bus);
        NeoForgeVoltaicCreativeTabs.attach(bus);
    }
}

