package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.client.AssemblyStationScreen;
import com.warpgames.voltaicforgery.voltaic.client.CastingFaucetBlockEntityRenderer;
import com.warpgames.voltaicforgery.voltaic.client.CastingFaucetStreamSettings;
import com.warpgames.voltaicforgery.voltaic.client.CastingTableBlockEntityRenderer;
import com.warpgames.voltaicforgery.voltaic.client.CastingTablePartTransforms;
import com.warpgames.voltaicforgery.voltaic.client.InductionCrucibleScreen;
import com.warpgames.voltaicforgery.voltaic.client.ModificationStationScreen;
import com.warpgames.voltaicforgery.voltaic.client.MoltenFluidColors;
import com.warpgames.voltaicforgery.voltaic.client.PatternTableScreen;
import net.neoforged.bus.api.SubscribeEvent;
import com.warpgames.voltaicforgery.voltaic.client.SolidFuelDynamoScreen;
import com.warpgames.voltaicforgery.voltaic.client.ToolPartTintSource;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class NeoForgeVoltaicClient {

    private NeoForgeVoltaicClient() {}

    static void attach(IEventBus bus) {
        bus.addListener(NeoForgeVoltaicClient::registerScreens);
        bus.addListener(NeoForgeVoltaicClient::registerItemTintSources);
        bus.addListener(NeoForgeVoltaicClient::registerFluidModels);
        bus.addListener(NeoForgeVoltaicClient::registerReloadListeners);
        bus.addListener(NeoForgeVoltaicClient::clientSetup);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(VoltaicContent.ASSEMBLY_STATION_MENU.get(), AssemblyStationScreen::new);
        event.register(VoltaicContent.SOLID_FUEL_DYNAMO_MENU.get(), SolidFuelDynamoScreen::new);
        event.register(VoltaicContent.INDUCTION_CRUCIBLE_MENU.get(), InductionCrucibleScreen::new);
        event.register(VoltaicContent.MODIFICATION_STATION_MENU.get(), ModificationStationScreen::new);
        event.register(VoltaicContent.PATTERN_TABLE_MENU.get(), PatternTableScreen::new);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(com.warpgames.voltaicforgery.voltaic.client.ModularToolTintSource.ID, com.warpgames.voltaicforgery.voltaic.client.ModularToolTintSource.MAP_CODEC);
        event.register(ToolPartTintSource.ID, ToolPartTintSource.MAP_CODEC);
    }

    private static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(createFluidModel("copper"), VoltaicContent.MOLTEN_COPPER::get, VoltaicContent.FLOWING_MOLTEN_COPPER::get);
        event.register(createFluidModel("iron"), VoltaicContent.MOLTEN_IRON::get, VoltaicContent.FLOWING_MOLTEN_IRON::get);
        event.register(createFluidModel("gold"), VoltaicContent.MOLTEN_GOLD::get, VoltaicContent.FLOWING_MOLTEN_GOLD::get);
        event.register(createFluidModel("tin"), VoltaicContent.MOLTEN_TIN::get, VoltaicContent.FLOWING_MOLTEN_TIN::get);
        event.register(createFluidModel("lead"), VoltaicContent.MOLTEN_LEAD::get, VoltaicContent.FLOWING_MOLTEN_LEAD::get);
        event.register(createFluidModel("silver"), VoltaicContent.MOLTEN_SILVER::get, VoltaicContent.FLOWING_MOLTEN_SILVER::get);
        event.register(createFluidModel("nickel"), VoltaicContent.MOLTEN_NICKEL::get, VoltaicContent.FLOWING_MOLTEN_NICKEL::get);
        event.register(createFluidModel("bronze"), VoltaicContent.MOLTEN_BRONZE::get, VoltaicContent.FLOWING_MOLTEN_BRONZE::get);
        event.register(createFluidModel("electrum"), VoltaicContent.MOLTEN_ELECTRUM::get, VoltaicContent.FLOWING_MOLTEN_ELECTRUM::get);
    }

    private static void registerReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(CastingTablePartTransforms.RELOAD_ID, (ResourceManagerReloadListener) resourceManager -> {
            CastingTablePartTransforms.reload(resourceManager);
            CastingFaucetStreamSettings.reload(resourceManager);
            MoltenFluidColors.reload(resourceManager);
        });
    }

    private static FluidModel.Unbaked createFluidModel(String metalName) {
        return new FluidModel.Unbaked(
                new Material(NeoForgeVoltaicFluidTypes.stillTexture()),
                new Material(NeoForgeVoltaicFluidTypes.flowingTexture()),
                new Material(NeoForgeVoltaicFluidTypes.overlayTexture()),
                state -> MoltenFluidColors.tint(state.getType())
        );
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(VoltaicContent.CASTING_TABLE_BE.get(), CastingTableBlockEntityRenderer::new);
            BlockEntityRenderers.register(VoltaicContent.CASTING_FAUCET_BE.get(), CastingFaucetBlockEntityRenderer::new);
        });
    }
}
