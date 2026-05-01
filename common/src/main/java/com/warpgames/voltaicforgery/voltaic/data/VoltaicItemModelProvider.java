package com.warpgames.voltaicforgery.voltaic.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.client.ToolPartTintSource;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class VoltaicItemModelProvider implements DataProvider {

    private final PackOutput.PathProvider itemDefinitions;
    private final PackOutput.PathProvider itemModels;

    public VoltaicItemModelProvider(PackOutput output) {
        this.itemDefinitions = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.itemModels = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        addModularPickaxe(futures, output);
        addLayeredModularTool(futures, output, "modular_axe", "item/tool_handle_part", "item/axe_head_part", "item/axe_binding_part");
        addLayeredModularTool(futures, output, "modular_shovel", "item/tool_handle_part", "item/shovel_head_part", "item/shovel_binding_part");
        addLayeredModularTool(futures, output, "modular_sword", "item/sword_handle_part", "item/sword_head_part", "item/sword_binding_part");

        // Cast models are authored in resources (Blockbench). Only emit item definitions so we don't duplicate/override them.
        addItemDefinitionOnly(futures, output, "pickaxe_head_cast");
        addItemDefinitionOnly(futures, output, "axe_head_cast");
        addItemDefinitionOnly(futures, output, "shovel_head_cast");
        addItemDefinitionOnly(futures, output, "sword_head_cast");
        addItemDefinitionOnly(futures, output, "tool_binding_cast");
        addItemDefinitionOnly(futures, output, "tool_handle_cast");
        addGeneratedItem(futures, output, "coil_upgrade_basic", "item/coil_upgrade_basic");
        addGeneratedItem(futures, output, "coil_upgrade_advanced", "item/coil_upgrade_advanced");
        addGeneratedItem(futures, output, "coil_upgrade_elite", "item/coil_upgrade_elite");
        addGeneratedItem(futures, output, "induction_coil", "item/coil_upgrade_basic");
        addTintedGeneratedItem(futures, output, "pickaxe_head_part", "item/pickaxe_head_part");
        addTintedGeneratedItem(futures, output, "axe_head_part", "item/axe_head_part");
        addTintedGeneratedItem(futures, output, "shovel_head_part", "item/shovel_head_part");
        addTintedGeneratedItem(futures, output, "sword_head_part", "item/sword_head_part");
        addTintedGeneratedItem(futures, output, "tool_binding_part", "item/tool_binding_part");
        addTintedGeneratedItem(futures, output, "tool_handle_part", "item/tool_handle_part");
        addItemDefinitionOnly(futures, output, "blank_cast");
        addBlockItemDirect(futures, output, "solid_fuel_dynamo");
        addBlockItem(futures, output, "induction_crucible");
        addBlockItem(futures, output, "casting_table");
        addBlockItem(futures, output, "casting_faucet");
        addBlockItem(futures, output, "assembly_station");
        addBlockItem(futures, output, "modification_station");
        addBlockItem(futures, output, "crucible_bricks");
        addGeneratedItem(futures, output, "unfired_crucible_brick", "item/unfired_crucible_brick");
        addGeneratedItem(futures, output, "crucible_brick", "item/crucible_brick");

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Voltaic Forgery Item Models";
    }

    private void addModularPickaxe(List<CompletableFuture<?>> futures, CachedOutput output) {
        futures.add(DataProvider.saveStable(output, modelJson(), modelPath()));
        futures.add(DataProvider.saveStable(output, itemDefinitionJson(), itemDefinitionPath()));
    }

    private void addLayeredModularTool(List<CompletableFuture<?>> futures, CachedOutput output, String itemPath, String layer0, String layer1, String layer2) {
        futures.add(DataProvider.saveStable(output, layeredModelJson(layer0, layer1, layer2), itemModels.json(VoltaicContent.id(itemPath))));
        futures.add(DataProvider.saveStable(output, itemDefinitionJson(itemPath), itemDefinitions.json(VoltaicContent.id(itemPath))));
    }

    private void addGeneratedItem(List<CompletableFuture<?>> futures, CachedOutput output, String itemPath, String texturePath) {
        futures.add(DataProvider.saveStable(output, generatedItemModel(texturePath), itemModels.json(VoltaicContent.id(itemPath))));
        futures.add(DataProvider.saveStable(output, itemDefinition("item/" + itemPath), itemDefinitions.json(VoltaicContent.id(itemPath))));
    }

    private void addItemDefinitionOnly(List<CompletableFuture<?>> futures, CachedOutput output, String itemPath) {
        futures.add(DataProvider.saveStable(output, itemDefinition("item/" + itemPath), itemDefinitions.json(VoltaicContent.id(itemPath))));
    }

    private void addTintedGeneratedItem(List<CompletableFuture<?>> futures, CachedOutput output, String itemPath, String texturePath) {
        futures.add(DataProvider.saveStable(output, generatedItemModel(texturePath), itemModels.json(VoltaicContent.id(itemPath))));
        futures.add(DataProvider.saveStable(output, tintedItemDefinition("item/" + itemPath), itemDefinitions.json(VoltaicContent.id(itemPath))));
    }

    private void addBlockItem(List<CompletableFuture<?>> futures, CachedOutput output, String blockPath) {
        futures.add(DataProvider.saveStable(output, blockItemModel(blockPath), itemModels.json(VoltaicContent.id(blockPath))));
        futures.add(DataProvider.saveStable(output, itemDefinition("item/" + blockPath), itemDefinitions.json(VoltaicContent.id(blockPath))));
    }

    private void addBlockItemDirect(List<CompletableFuture<?>> futures, CachedOutput output, String blockPath) {
        futures.add(DataProvider.saveStable(output, blockItemModel(blockPath), itemModels.json(VoltaicContent.id(blockPath))));
        futures.add(DataProvider.saveStable(output, itemDefinition("block/" + blockPath), itemDefinitions.json(VoltaicContent.id(blockPath))));
    }

    private Path modelPath() {
        return itemModels.json(VoltaicContent.id("modular_pickaxe"));
    }

    private Path itemDefinitionPath() {
        return itemDefinitions.json(VoltaicContent.id("modular_pickaxe"));
    }

    private static JsonObject modelJson() {
        return layeredModelJson("item/tool_handle_part", "item/pickaxe_head_part", "item/pickaxe_binding_part");
    }

    private static JsonObject layeredModelJson(String layer0, String layer1, String layer2) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", VoltaicContent.id(layer0).toString());
        textures.addProperty("layer1", VoltaicContent.id(layer1).toString());
        textures.addProperty("layer2", VoltaicContent.id(layer2).toString());
        root.add("textures", textures);
        return root;
    }

    private static JsonObject generatedItemModel(String texturePath) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", VoltaicContent.id(texturePath).toString());
        root.add("textures", textures);
        return root;
    }

    private static JsonObject blockItemModel(String blockPath) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", VoltaicContent.id("block/" + blockPath).toString());
        return root;
    }

    private static JsonObject itemDefinitionJson() {
        return itemDefinitionJson("modular_pickaxe");
    }

    private static JsonObject itemDefinitionJson(String name) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", VoltaicContent.id("item/" + name).toString());

        JsonArray tints = new JsonArray();
        tints.add(tintSource(0));
        tints.add(tintSource(2));
        tints.add(tintSource(1));
        model.add("tints", tints);

        JsonObject root = new JsonObject();
        root.add("model", model);
        return root;
    }

    private static JsonObject itemDefinition(String modelPath) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", VoltaicContent.id(modelPath).toString());

        JsonObject root = new JsonObject();
        root.add("model", model);
        return root;
    }

    private static JsonObject tintedItemDefinition(String modelPath) {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", VoltaicContent.id(modelPath).toString());

        JsonArray tints = new JsonArray();
        JsonObject tint = new JsonObject();
        tint.addProperty("type", ToolPartTintSource.ID.toString());
        tints.add(tint);
        model.add("tints", tints);

        JsonObject root = new JsonObject();
        root.add("model", model);
        return root;
    }

    private static JsonObject tintSource(int layer) {
        JsonObject tint = new JsonObject();
        tint.addProperty("type", com.warpgames.voltaicforgery.voltaic.client.ModularToolTintSource.ID.toString());
        tint.addProperty("layer", layer);
        return tint;
    }
}
