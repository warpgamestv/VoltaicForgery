package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.client.CastingTablePartTransforms;
import com.warpgames.voltaicforgery.voltaic.client.CastingFaucetStreamSettings;
import com.warpgames.voltaicforgery.voltaic.client.MoltenFluidColors;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public final class FabricCastingTablePartTransforms implements SimpleSynchronousResourceReloadListener {

    public static final FabricCastingTablePartTransforms INSTANCE = new FabricCastingTablePartTransforms();

    private FabricCastingTablePartTransforms() {}

    @Override
    public Identifier getFabricId() {
        return CastingTablePartTransforms.RELOAD_ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CastingTablePartTransforms.reload(resourceManager);
        CastingFaucetStreamSettings.reload(resourceManager);
        MoltenFluidColors.reload(resourceManager);
    }
}
