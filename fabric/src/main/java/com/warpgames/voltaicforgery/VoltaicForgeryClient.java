package com.warpgames.voltaicforgery;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.client.CastingFaucetBlockEntityRenderer;
import com.warpgames.voltaicforgery.voltaic.client.CastingTableBlockEntityRenderer;
import com.warpgames.voltaicforgery.voltaic.client.AssemblyStationScreen;
import com.warpgames.voltaicforgery.voltaic.client.InductionCrucibleScreen;
import com.warpgames.voltaicforgery.voltaic.client.ModificationStationScreen;
import com.warpgames.voltaicforgery.voltaic.client.PatternTableScreen;
import com.warpgames.voltaicforgery.voltaic.client.SolidFuelDynamoScreen;
import com.warpgames.voltaicforgery.platform.FabricVoltaicClientTintSources;
import com.warpgames.voltaicforgery.platform.FabricFluidRendering;
import com.warpgames.voltaicforgery.platform.FabricCastingTablePartTransforms;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.server.packs.PackType;

public class VoltaicForgeryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricVoltaicClientTintSources.register();
        FabricFluidRendering.register();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FabricCastingTablePartTransforms.INSTANCE);
        
        MenuScreens.register(VoltaicContent.ASSEMBLY_STATION_MENU.get(), AssemblyStationScreen::new);
        MenuScreens.register(VoltaicContent.SOLID_FUEL_DYNAMO_MENU.get(), SolidFuelDynamoScreen::new);
        MenuScreens.register(VoltaicContent.INDUCTION_CRUCIBLE_MENU.get(), InductionCrucibleScreen::new);
        MenuScreens.register(VoltaicContent.MODIFICATION_STATION_MENU.get(), ModificationStationScreen::new);
        MenuScreens.register(VoltaicContent.PATTERN_TABLE_MENU.get(), PatternTableScreen::new);

        BlockEntityRenderers.register(VoltaicContent.CASTING_TABLE_BE.get(), CastingTableBlockEntityRenderer::new);
        BlockEntityRenderers.register(VoltaicContent.CASTING_FAUCET_BE.get(), CastingFaucetBlockEntityRenderer::new);
    }
}
