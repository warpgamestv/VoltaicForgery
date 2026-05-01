package com.warpgames.voltaicforgery;

import com.warpgames.voltaicforgery.platform.Services;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

// Shared bootstrap for common code used by every supported loader.
public class CommonClass {

    public static void init() {

        Constants.LOG.info("Voltaic Forgery common init on {} in {} mode.", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.debug("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        VoltaicContent.init();
        Services.CAPABILITIES.init();
    }
}

