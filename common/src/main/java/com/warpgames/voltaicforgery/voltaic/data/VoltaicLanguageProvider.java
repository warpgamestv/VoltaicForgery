package com.warpgames.voltaicforgery.voltaic.data;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class VoltaicLanguageProvider implements DataProvider {

    private final PackOutput.PathProvider lang;

    public VoltaicLanguageProvider(PackOutput output) {
        this.lang = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return DataProvider.saveStable(output, translations(), enUsPath());
    }

    @Override
    public String getName() {
        return "Voltaic Forgery English Language";
    }

    private Path enUsPath() {
        return lang.file(net.minecraft.resources.Identifier.fromNamespaceAndPath("voltaicforgery", "en_us"), "json");
    }

    private static JsonObject translations() {
        JsonObject json = new JsonObject();

        add(json, "block.voltaicforgery.solid_fuel_dynamo", "Solid Fuel Dynamo");
        add(json, "block.voltaicforgery.induction_crucible", "Induction Crucible");
        add(json, "block.voltaicforgery.casting_table", "Casting Table");
        add(json, "block.voltaicforgery.assembly_station", "Assembly Station");
        add(json, "block.voltaicforgery.modification_station", "Modification Station");
        add(json, "block.voltaicforgery.pattern_table", "Pattern Table");
        add(json, "block.voltaicforgery.crucible_bricks", "Crucible Bricks");
        add(json, "block.voltaicforgery.casting_faucet", "Casting Faucet");

        add(json, "item.voltaicforgery.solid_fuel_dynamo", "Solid Fuel Dynamo");
        add(json, "item.voltaicforgery.induction_crucible", "Induction Crucible");
        add(json, "item.voltaicforgery.casting_table", "Casting Table");
        add(json, "item.voltaicforgery.assembly_station", "Assembly Station");
        add(json, "item.voltaicforgery.modification_station", "Modification Station");
        add(json, "item.voltaicforgery.pattern_table", "Pattern Table");
        add(json, "item.voltaicforgery.crucible_bricks", "Crucible Bricks");
        add(json, "item.voltaicforgery.casting_faucet", "Casting Faucet");

        add(json, "fluid.voltaicforgery.molten_copper", "Molten Copper");
        add(json, "fluid.voltaicforgery.molten_iron", "Molten Iron");
        add(json, "fluid.voltaicforgery.molten_gold", "Molten Gold");
        add(json, "fluid.voltaicforgery.molten_tin", "Molten Tin");
        add(json, "fluid.voltaicforgery.molten_lead", "Molten Lead");
        add(json, "fluid.voltaicforgery.molten_silver", "Molten Silver");
        add(json, "fluid.voltaicforgery.molten_nickel", "Molten Nickel");
        add(json, "fluid.voltaicforgery.molten_bronze", "Molten Bronze");
        add(json, "fluid.voltaicforgery.molten_electrum", "Molten Electrum");
        add(json, "item.voltaicforgery.coil_upgrade_basic", "Basic Coil Upgrade");
        add(json, "item.voltaicforgery.coil_upgrade_advanced", "Advanced Coil Upgrade");
        add(json, "item.voltaicforgery.coil_upgrade_elite", "Elite Coil Upgrade");
        add(json, "item.voltaicforgery.pickaxe_head_part", "Pickaxe Head Part");
        add(json, "item.voltaicforgery.axe_head_part", "Axe Head Part");
        add(json, "item.voltaicforgery.shovel_head_part", "Shovel Head Part");
        add(json, "item.voltaicforgery.sword_head_part", "Sword Head Part");
        add(json, "item.voltaicforgery.tool_binding_part", "Tool Binding Part");
        add(json, "item.voltaicforgery.tool_handle_part", "Tool Handle Part");
        add(json, "item.voltaicforgery.modular_pickaxe", "Modular Pickaxe");
        add(json, "item.voltaicforgery.modular_axe", "Modular Axe");
        add(json, "item.voltaicforgery.modular_shovel", "Modular Shovel");
        add(json, "item.voltaicforgery.modular_sword", "Modular Sword");
        add(json, "item.voltaicforgery.pickaxe_head_cast", "Pickaxe Head Cast");
        add(json, "item.voltaicforgery.axe_head_cast", "Axe Head Cast");
        add(json, "item.voltaicforgery.shovel_head_cast", "Shovel Head Cast");
        add(json, "item.voltaicforgery.sword_head_cast", "Sword Head Cast");
        add(json, "item.voltaicforgery.tool_binding_cast", "Tool Binding Cast");
        add(json, "item.voltaicforgery.tool_handle_cast", "Tool Handle Cast");
        add(json, "item.voltaicforgery.blank_pattern", "Blank Pattern");
        add(json, "item.voltaicforgery.pickaxe_head_pattern", "Pickaxe Head Pattern");
        add(json, "item.voltaicforgery.axe_head_pattern", "Axe Head Pattern");
        add(json, "item.voltaicforgery.shovel_head_pattern", "Shovel Head Pattern");
        add(json, "item.voltaicforgery.sword_head_pattern", "Sword Head Pattern");
        add(json, "item.voltaicforgery.tool_binding_pattern", "Tool Binding Pattern");
        add(json, "item.voltaicforgery.tool_handle_pattern", "Tool Handle Pattern");
        add(json, "item.voltaicforgery.unfired_crucible_brick", "Unfired Crucible Brick");
        add(json, "item.voltaicforgery.crucible_brick", "Crucible Brick");

        add(json, "container.voltaicforgery.solid_fuel_dynamo", "Solid Fuel Dynamo");
        add(json, "container.voltaicforgery.induction_crucible", "Induction Crucible");
        add(json, "container.voltaicforgery.assembly_station", "Assembly Station");
        add(json, "container.voltaicforgery.modification_station", "Modification Station");
        add(json, "container.voltaicforgery.casting_table", "Casting Table");
        add(json, "container.voltaicforgery.pattern_table", "Pattern Table");

        add(json, "gui.voltaicforgery.energy", "Energy:");
        add(json, "gui.voltaicforgery.heat", "Heat:");
        add(json, "gui.voltaicforgery.stored_fluid", "Stored Fluid:");
        add(json, "gui.voltaicforgery.cooling", "Cooling:");
        add(json, "gui.voltaicforgery.modification_station.insert_tool", "Insert a modular tool");
        add(json, "gui.voltaicforgery.modification_station.insert_material", "Insert a material");
        add(json, "gui.voltaicforgery.modification_station.no_modifiers", "No modifiers");
        add(json, "gui.voltaicforgery.modification_station.slots", "Slots");
        add(json, "gui.voltaicforgery.modification_station.modifier", "%s Tier %s");
        add(json, "gui.voltaicforgery.modification_station.modifier_haste", "%s Tier %s: +%s%%");
        add(json, "gui.voltaicforgery.modification_station.invalid_material", "Not a modifier material");
        add(json, "gui.voltaicforgery.modification_station.wrong_tool", "Wrong tool type");
        add(json, "gui.voltaicforgery.modification_station.max_tier", "Modifier maxed");
        add(json, "gui.voltaicforgery.modification_station.no_slots", "No modifier slots");
        add(json, "gui.voltaicforgery.modification_station.blocked", "Cannot apply");
        add(json, "jei.voltaicforgery.pattern_tool_part", "Pattern Table");
        add(json, "jei.voltaicforgery.casting", "Casting");
        add(json, "jei.voltaicforgery.melting", "Melting");
        add(json, "jei.voltaicforgery.tool_modifier", "Tool Modifiers");

        add(json, "tooltip.voltaicforgery.traits", "Traits:");
        add(json, "tooltip.voltaicforgery.tool_energy", "Charge: %s / %s FE");
        add(json, "tooltip.voltaicforgery.tool_parts", "Parts:");
        add(json, "tooltip.voltaicforgery.tool_part_head", " - Head: %s");
        add(json, "tooltip.voltaicforgery.tool_part_binding", " - Binding: %s");
        add(json, "tooltip.voltaicforgery.tool_part_handle", " - Handle: %s");
        add(json, "tooltip.voltaicforgery.modifier_slots", "Modifier Slots: %s");
        add(json, "tooltip.voltaicforgery.modifiers", "Modifiers:");
        add(json, "tooltip.voltaicforgery.modifier", "%s Tier %s (%s)");
        add(json, "tooltip.voltaicforgery.modifier_haste", "%s Tier %s (%s, +%s%% speed)");
        add(json, "trait.voltaicforgery.auto_smelt", "Auto-Smelt");
        add(json, "trait.voltaicforgery.magnetic", "Magnetic");
        add(json, "trait.voltaicforgery.haste", "Haste");
        add(json, "trait.voltaicforgery.powered", "Powered");

        add(json, "itemGroup.voltaicforgery.main", "Voltaic Forgery");
        add(json, "itemGroup.voltaicforgery.voltaic_forgery", "Voltaic Forgery");

        return json;
    }

    private static void add(JsonObject json, String key, String value) {
        json.addProperty(key, value);
    }
}
