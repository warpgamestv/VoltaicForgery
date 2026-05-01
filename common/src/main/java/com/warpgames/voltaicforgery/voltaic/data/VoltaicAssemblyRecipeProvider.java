package com.warpgames.voltaicforgery.voltaic.data;

import com.google.gson.JsonObject;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoltaicAssemblyRecipeProvider implements DataProvider {

    private final PackOutput.PathProvider recipePathProvider;

    public VoltaicAssemblyRecipeProvider(PackOutput output) {
        this.recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        addAssemblyRecipe(futures, output, "modular_pickaxe", VoltaicContent.PICKAXE_HEAD_PART.get(), VoltaicContent.TOOL_BINDING_PART.get(), VoltaicContent.TOOL_HANDLE_PART.get(), VoltaicContent.MODULAR_PICKAXE.get());
        addAssemblyRecipe(futures, output, "modular_axe", VoltaicContent.AXE_HEAD_PART.get(), VoltaicContent.TOOL_BINDING_PART.get(), VoltaicContent.TOOL_HANDLE_PART.get(), VoltaicContent.MODULAR_AXE.get());
        addAssemblyRecipe(futures, output, "modular_shovel", VoltaicContent.SHOVEL_HEAD_PART.get(), VoltaicContent.TOOL_BINDING_PART.get(), VoltaicContent.TOOL_HANDLE_PART.get(), VoltaicContent.MODULAR_SHOVEL.get());
        addAssemblyRecipe(futures, output, "modular_sword", VoltaicContent.SWORD_HEAD_PART.get(), VoltaicContent.TOOL_BINDING_PART.get(), VoltaicContent.TOOL_HANDLE_PART.get(), VoltaicContent.MODULAR_SWORD.get());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void addAssemblyRecipe(List<CompletableFuture<?>> futures, CachedOutput output, String name, Item head, Item binding, Item handle, Item result) {
        JsonObject json = new JsonObject();
        json.addProperty("type", VoltaicContent.id("assembly").toString());
        json.addProperty("head", BuiltInRegistries.ITEM.getKey(head).toString());
        json.addProperty("binding", BuiltInRegistries.ITEM.getKey(binding).toString());
        json.addProperty("handle", BuiltInRegistries.ITEM.getKey(handle).toString());
        json.addProperty("result", BuiltInRegistries.ITEM.getKey(result).toString());
        json.addProperty("modifier_slots", 3);

        Path path = recipePathProvider.json(VoltaicContent.id(name));
        futures.add(DataProvider.saveStable(output, json, path));
    }

    @Override
    public String getName() {
        return "Voltaic Forgery Assembly Recipes";
    }
}
