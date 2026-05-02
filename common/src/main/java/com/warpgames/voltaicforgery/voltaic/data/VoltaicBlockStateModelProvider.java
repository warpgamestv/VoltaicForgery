package com.warpgames.voltaicforgery.voltaic.data;

import com.google.gson.JsonObject;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class VoltaicBlockStateModelProvider implements DataProvider {

    private final PackOutput.PathProvider blockStates;
    private final PackOutput.PathProvider blockModels;

    public VoltaicBlockStateModelProvider(PackOutput output) {
        this.blockStates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModels = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        addDynamo(futures, output);
        addBlockStateOnly(futures, output, "induction_crucible");
        // Base casting table model comes from Blockbench JSON in resources.
        addBlockStateOnly(futures, output, "casting_table");
        addBlockStateOnly(futures, output, "assembly_station");
        addHorizontalFacing(futures, output, "casting_faucet");
        addBlockStateOnly(futures, output, "modification_station");
        addCubeAllMachine(futures, output, "crucible_bricks", VoltaicContent.id("block/crucible_bricks").toString());
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Voltaic Forgery Blockstates and Block Models";
    }

    private void addCubeAllMachine(List<CompletableFuture<?>> futures, CachedOutput output, String blockPath, String texture) {
        futures.add(DataProvider.saveStable(output, blockState(blockPath), blockStates.json(VoltaicContent.id(blockPath))));
        futures.add(DataProvider.saveStable(output, cubeAllModel(texture), blockModels.json(VoltaicContent.id(blockPath))));
    }

    private void addBlockStateOnly(List<CompletableFuture<?>> futures, CachedOutput output, String blockPath) {
        futures.add(DataProvider.saveStable(output, blockState(blockPath), blockStates.json(VoltaicContent.id(blockPath))));
    }

    private void addHorizontalFacing(List<CompletableFuture<?>> futures, CachedOutput output, String blockPath) {
        futures.add(DataProvider.saveStable(output, horizontalFacingBlockState(blockPath), blockStates.json(VoltaicContent.id(blockPath))));
    }

    private void addDynamo(List<CompletableFuture<?>> futures, CachedOutput output) {
        futures.add(DataProvider.saveStable(output, dynamoBlockState(), blockStates.json(VoltaicContent.id("solid_fuel_dynamo"))));
        // Base dynamo model comes from Blockbench JSON in resources; only generate the lit override model.
        futures.add(DataProvider.saveStable(output, dynamoModel("block/dynamo_on"), blockModels.json(VoltaicContent.id("solid_fuel_dynamo_on"))));
    }

    private static JsonObject blockState(String blockPath) {
        JsonObject model = new JsonObject();
        model.addProperty("model", VoltaicContent.id("block/" + blockPath).toString());

        JsonObject variants = new JsonObject();
        variants.add("", model);

        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    private static JsonObject horizontalFacingBlockState(String blockPath) {
        String modelId = VoltaicContent.id("block/" + blockPath).toString();
        JsonObject variants = new JsonObject();

        // Base model has attachment at +X (east face).
        // facing=east -> 0°, south -> 90°, west -> 180°, north -> 270°
        addFacingVariant(variants, modelId, "east", 0);
        addFacingVariant(variants, modelId, "south", 90);
        addFacingVariant(variants, modelId, "west", 180);
        addFacingVariant(variants, modelId, "north", 270);

        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    private static void addFacingVariant(JsonObject variants, String modelId, String facing, int yRot) {
        JsonObject entry = new JsonObject();
        entry.addProperty("model", modelId);
        if (yRot != 0) {
            entry.addProperty("y", yRot);
        }
        variants.add("facing=" + facing, entry);
    }

    private static JsonObject dynamoBlockState() {
        JsonObject variants = new JsonObject();

        addDynamoVariant(variants, "north", false, 0);
        addDynamoVariant(variants, "east", false, 90);
        addDynamoVariant(variants, "south", false, 180);
        addDynamoVariant(variants, "west", false, 270);

        addDynamoVariant(variants, "north", true, 0);
        addDynamoVariant(variants, "east", true, 90);
        addDynamoVariant(variants, "south", true, 180);
        addDynamoVariant(variants, "west", true, 270);

        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    private static void addDynamoVariant(JsonObject variants, String facing, boolean lit, int yRot) {
        JsonObject entry = new JsonObject();
        entry.addProperty("model", lit
                ? VoltaicContent.id("block/solid_fuel_dynamo_on").toString()
                : VoltaicContent.id("block/solid_fuel_dynamo").toString()
        );
        if (yRot != 0) {
            entry.addProperty("y", yRot);
        }
        variants.add("facing=" + facing + ",lit=" + lit, entry);
    }

    private static JsonObject dynamoModel(String texture) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", VoltaicContent.id("block/solid_fuel_dynamo").toString());

        JsonObject textures = new JsonObject();
        textures.addProperty("0", VoltaicContent.id(texture).toString());
        textures.addProperty("particle", VoltaicContent.id(texture).toString());
        root.add("textures", textures);
        return root;
    }

    private static JsonObject cubeAllModel(String texture) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/cube_all");

        JsonObject textures = new JsonObject();
        textures.addProperty("all", texture);
        root.add("textures", textures);

        return root;
    }
}
