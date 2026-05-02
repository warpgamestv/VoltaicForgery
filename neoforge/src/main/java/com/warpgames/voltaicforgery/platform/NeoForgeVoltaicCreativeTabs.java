package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class NeoForgeVoltaicCreativeTabs {

    private NeoForgeVoltaicCreativeTabs() {}

    private static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, VoltaicContent.id("voltaic_forgery"));

    public static void attach(IEventBus bus) {
        bus.addListener(NeoForgeVoltaicCreativeTabs::onBuildTabContents);
    }

    private static void onBuildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(TAB_KEY)) return;

        event.accept(VoltaicContent.SOLID_FUEL_DYNAMO.get().asItem());
        event.accept(VoltaicContent.INDUCTION_CRUCIBLE.get().asItem());
        event.accept(VoltaicContent.CASTING_FAUCET.get());
        event.accept(VoltaicContent.CASTING_TABLE.get().asItem());
        event.accept(VoltaicContent.ASSEMBLY_STATION.get().asItem());
        event.accept(VoltaicContent.MODIFICATION_STATION.get().asItem());
        event.accept(VoltaicContent.PATTERN_TABLE.get().asItem());
        event.accept(VoltaicContent.CRUCIBLE_BRICKS.get().asItem());

        event.accept(VoltaicContent.UNFIRED_CRUCIBLE_BRICK.get());
        event.accept(VoltaicContent.CRUCIBLE_BRICK.get());

        event.accept(VoltaicContent.BLANK_PATTERN.get());
        event.accept(VoltaicContent.PICKAXE_HEAD_CAST.get());
        event.accept(VoltaicContent.AXE_HEAD_CAST.get());
        event.accept(VoltaicContent.SHOVEL_HEAD_CAST.get());
        event.accept(VoltaicContent.SWORD_HEAD_CAST.get());
        event.accept(VoltaicContent.TOOL_BINDING_CAST.get());
        event.accept(VoltaicContent.TOOL_HANDLE_CAST.get());

        // Tool parts: single base item IDs, material specified via data component.
        addToolPartsForAllMaterials(event, event.getParameters().holders());

        event.accept(VoltaicContent.MODULAR_PICKAXE.get());
        event.accept(VoltaicContent.MODULAR_AXE.get());
        event.accept(VoltaicContent.MODULAR_SHOVEL.get());
        event.accept(VoltaicContent.MODULAR_SWORD.get());

        event.accept(VoltaicContent.COIL_UPGRADE_BASIC.get());
        event.accept(VoltaicContent.COIL_UPGRADE_ADVANCED.get());
        event.accept(VoltaicContent.COIL_UPGRADE_ELITE.get());
    }

    private static void addToolPartsForAllMaterials(BuildCreativeModeTabContentsEvent event, HolderLookup.Provider registries) {
        var materials = registries.lookup(VoltaicRegistries.TOOL_MATERIALS)
                .stream()
                .flatMap(HolderLookup.RegistryLookup::listElements)
                .map(Holder::value)
                .toList();

        for (ToolMaterialStat material : materials) {
            String id = material.materialId();
            acceptPart(event, VoltaicContent.PICKAXE_HEAD_PART.get(), id);
            acceptPart(event, VoltaicContent.AXE_HEAD_PART.get(), id);
            acceptPart(event, VoltaicContent.SHOVEL_HEAD_PART.get(), id);
            acceptPart(event, VoltaicContent.SWORD_HEAD_PART.get(), id);
            acceptPart(event, VoltaicContent.TOOL_BINDING_PART.get(), id);
            acceptPart(event, VoltaicContent.TOOL_HANDLE_PART.get(), id);
        }
    }

    private static void acceptPart(BuildCreativeModeTabContentsEvent event, Item item, String materialId) {
        event.accept(ToolPartItem.createStack(item, materialId));
    }
}
