package com.warpgames.voltaicforgery;

import com.warpgames.voltaicforgery.voltaic.registry.VoltaicRegistries;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

public class VoltaicForgery implements ModInitializer {

    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(VoltaicRegistries.TOOL_MATERIALS, ToolMaterialStat.CODEC, ToolMaterialStat.CODEC);
        DynamicRegistries.registerSynced(VoltaicRegistries.TOOL_MODIFIERS, ToolModifierEntry.CODEC, ToolModifierEntry.CODEC);
        Constants.LOG.info("Voltaic Forgery loaded (Fabric).");
        CommonClass.init();

        var tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, VoltaicContent.id("voltaic_forgery"));
        CreativeModeTabEvents.modifyOutputEvent(tabKey).register(this::populateTab);
    }

    private void populateTab(Object entries) {
        try {
            // Find the accept(ItemStack) method on FabricItemGroupEntries (which implements CreativeModeTab.Output)
            Method accept = null;
            for (Method m : entries.getClass().getMethods()) {
                if ((m.getName().equals("accept") || m.getName().equals("m_246342_")) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == ItemStack.class) {
                    accept = m;
                    break;
                }
            }
            if (accept == null) return;

            accept.invoke(entries, new ItemStack(VoltaicContent.SOLID_FUEL_DYNAMO.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.INDUCTION_CRUCIBLE.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.CASTING_FAUCET.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.CASTING_TABLE.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.ASSEMBLY_STATION.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.MODIFICATION_STATION.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.PATTERN_TABLE.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.CRUCIBLE_BRICKS.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.UNFIRED_CRUCIBLE_BRICK.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.CRUCIBLE_BRICK.get()));

            accept.invoke(entries, new ItemStack(VoltaicContent.BLANK_PATTERN.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.PICKAXE_HEAD_CAST.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.AXE_HEAD_CAST.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.SHOVEL_HEAD_CAST.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.SWORD_HEAD_CAST.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.TOOL_BINDING_CAST.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.TOOL_HANDLE_CAST.get()));

            addToolPartsForAllMaterials(accept, entries);

            accept.invoke(entries, new ItemStack(VoltaicContent.MODULAR_PICKAXE.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.MODULAR_AXE.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.MODULAR_SHOVEL.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.MODULAR_SWORD.get()));

            accept.invoke(entries, new ItemStack(VoltaicContent.COIL_UPGRADE_BASIC.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.COIL_UPGRADE_ADVANCED.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.COIL_UPGRADE_ELITE.get()));
        } catch (Exception e) {
            Constants.LOG.error("Error populating creative tab", e);
        }
    }

    private void addToolPartsForAllMaterials(Method accept, Object entries) throws Exception {
        // Use reflection to get registries from entries.getContext().lookup() if available (FabricItemGroupEntries)
        HolderLookup.Provider registries = null;
        try {
            Object context = entries.getClass().getMethod("getContext").invoke(entries);
            registries = (HolderLookup.Provider) context.getClass().getMethod("lookup").invoke(context);
        } catch (Throwable ignored) {
            // Fallback to client level if on client
            try {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object level = mcClass.getField("level").get(mc);
                if (level != null) {
                    registries = (HolderLookup.Provider) level.getClass().getMethod("registryAccess").invoke(level);
                }
            } catch (Throwable ignored2) {}
        }

        if (registries == null) {
            accept.invoke(entries, new ItemStack(VoltaicContent.PICKAXE_HEAD_PART.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.AXE_HEAD_PART.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.SHOVEL_HEAD_PART.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.SWORD_HEAD_PART.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.TOOL_BINDING_PART.get()));
            accept.invoke(entries, new ItemStack(VoltaicContent.TOOL_HANDLE_PART.get()));
            return;
        }

        var materials = registries.lookup(VoltaicRegistries.TOOL_MATERIALS)
                .stream()
                .flatMap(HolderLookup.RegistryLookup::listElements)
                .map(Holder::value)
                .toList();

        for (ToolMaterialStat material : materials) {
            String id = material.materialId();
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.PICKAXE_HEAD_PART.get(), id));
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.AXE_HEAD_PART.get(), id));
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.SHOVEL_HEAD_PART.get(), id));
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.SWORD_HEAD_PART.get(), id));
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.TOOL_BINDING_PART.get(), id));
            accept.invoke(entries, com.warpgames.voltaicforgery.voltaic.item.ToolPartItem.createStack(VoltaicContent.TOOL_HANDLE_PART.get(), id));
        }
    }
}
