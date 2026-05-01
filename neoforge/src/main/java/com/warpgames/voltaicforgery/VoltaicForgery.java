package com.warpgames.voltaicforgery;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class VoltaicForgery {

    public VoltaicForgery(IEventBus eventBus) {

        com.warpgames.voltaicforgery.platform.NeoForgeVoltaicBootstrap.init(eventBus);

        Constants.LOG.info("Voltaic Forgery loaded (NeoForge).");
        CommonClass.init();

        // Debug: confirm JEI plugin annotation scan sees our plugin class.
        // If this logs 0, JEI won't discover our IModPlugin via ModList scanning.
        try {
            var annotationType = org.objectweb.asm.Type.getType(Class.forName("mezz.jei.api.JeiPlugin"));
            var scanData = net.neoforged.fml.ModList.get().getAllScanData();
            int count = 0;
            boolean foundOurs = false;
            for (var data : scanData) {
                for (var a : data.getAnnotations()) {
                    if (java.util.Objects.equals(a.annotationType(), annotationType)) {
                        count++;
                        if ("com.warpgames.voltaicforgery.compat.jei.VoltaicJeiPlugin".equals(a.memberName())) {
                            foundOurs = true;
                        }
                    }
                }
            }
            Constants.LOG.info("[JEI] @JeiPlugin annotations found: {} (found ours: {})", count, foundOurs);
        } catch (Throwable t) {
            Constants.LOG.warn("[JEI] Unable to check @JeiPlugin scan data", t);
        }
    }
}

