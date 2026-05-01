package com.warpgames.voltaicforgery.voltaic.data;

import com.google.gson.JsonObject;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class VoltaicCastingRecipeProvider implements DataProvider {

    private final PackOutput.PathProvider recipes;

    public VoltaicCastingRecipeProvider(PackOutput output) {
        this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    // Each material that has a registered molten fluid and can produce tool parts.
    private record CastingMaterial(String name, String fluidId) {}

    private static final CastingMaterial[] MATERIALS = {
            new CastingMaterial("copper", "molten_copper"),
            new CastingMaterial("iron", "molten_iron"),
            new CastingMaterial("gold", "molten_gold")
    };

    // Part definitions: cast item, result item, fluid cost in mb.
    private record PartDef(String castItem, String resultItem, int fluidMb) {}

    private static final PartDef[] PARTS = {
            new PartDef("pickaxe_head_cast", "pickaxe_head_part", 216),
            new PartDef("axe_head_cast",     "axe_head_part",     288),
            new PartDef("shovel_head_cast",  "shovel_head_part",  144),
            new PartDef("sword_head_cast",   "sword_head_part",   288),
            new PartDef("tool_binding_cast", "tool_binding_part",  72),
            new PartDef("tool_handle_cast",  "tool_handle_part",   72)
    };

    private record CastMoldDef(String partItem, String castItem) {}

    private static final CastMoldDef[] CAST_MOLDS = {
            new CastMoldDef("pickaxe_head_part", "pickaxe_head_cast"),
            new CastMoldDef("axe_head_part",     "axe_head_cast"),
            new CastMoldDef("shovel_head_part",  "shovel_head_cast"),
            new CastMoldDef("sword_head_part",   "sword_head_cast"),
            new CastMoldDef("tool_binding_part", "tool_binding_cast"),
            new CastMoldDef("tool_handle_part",  "tool_handle_cast")
    };

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (CastingMaterial material : MATERIALS) {
            for (PartDef part : PARTS) {
                String path = "casting/" + material.name + "_" + part.resultItem.replace("_part", "");
                futures.add(saveCastingRecipe(output, path, part.castItem, part.resultItem, material.fluidId, part.fluidMb, material.name, false));
            }
        }
        for (CastMoldDef mold : CAST_MOLDS) {
            String path = "casting/casts/" + mold.castItem.replace("_cast", "");
            futures.add(saveCastingRecipe(output, path, mold.partItem, mold.castItem, "molten_gold", 144, "", true));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Voltaic Forgery Casting Recipes";
    }

    private CompletableFuture<?> saveCastingRecipe(CachedOutput output, String path, String castItem, String resultItem, String fluidId, int fluidAmountMb, String materialName, boolean consumeCast) {
        JsonObject root = new JsonObject();
        root.addProperty("type", VoltaicContent.id("casting").toString());

        root.addProperty("cast", VoltaicContent.id(castItem).toString());

        JsonObject fluid = new JsonObject();
        fluid.addProperty("fluid", VoltaicContent.id(fluidId).toString());
        fluid.addProperty("amount", fluidAmountMb);
        root.add("fluid", fluid);

        JsonObject result = new JsonObject();
        result.addProperty("id", VoltaicContent.id(resultItem).toString());
        result.addProperty("count", 1);
        root.add("result", result);

        if (!materialName.isBlank()) {
            root.addProperty("result_material", materialName);
        }
        if (consumeCast) {
            root.addProperty("consume_cast", true);
        }

        Path out = recipes.json(VoltaicContent.id(path));
        return DataProvider.saveStable(output, root, out);
    }
}
