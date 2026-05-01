package com.warpgames.voltaicforgery.voltaic.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public final class CastingTablePartTransforms {

    public static final Identifier RELOAD_ID = VoltaicContent.id("casting_table_part_transforms");
    private static final Gson GSON = new Gson();
    private static volatile Map<Identifier, Transform> transforms = defaultTransforms();

    private CastingTablePartTransforms() {}

    public static Transform get(Identifier itemId) {
        return transforms.getOrDefault(itemId, Transform.NONE);
    }

    public static void reload(ResourceManager resourceManager) {
        Map<Identifier, Transform> loaded = defaultTransforms();
        Map<Identifier, Resource> resources = resourceManager.listResources(
                "render/casting_table_part_transforms",
                id -> id.getPath().endsWith(".json")
        );

        resources.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> loadResource(loaded, entry.getKey(), entry.getValue()));

        transforms = Map.copyOf(loaded);
    }

    private static void loadResource(Map<Identifier, Transform> loaded, Identifier id, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("replace") && root.get("replace").getAsBoolean()) {
                loaded.clear();
            }

            JsonObject entries = root.has("entries") ? root.getAsJsonObject("entries") : root;
            for (Map.Entry<String, JsonElement> entry : entries.entrySet()) {
                if (entry.getKey().equals("replace") || entry.getKey().equals("entries")) continue;
                Identifier itemId = Identifier.parse(entry.getKey());
                loaded.put(itemId, GSON.fromJson(entry.getValue(), Transform.class).normalized());
            }
        } catch (Exception e) {
            Constants.LOG.warn("Failed to load casting table part transforms from {}", id, e);
        }
    }

    private static Map<Identifier, Transform> defaultTransforms() {
        float pixel = 1.0F / 16.0F;
        Map<Identifier, Transform> defaults = new HashMap<>();
        putPair(defaults, "pickaxe_head_part", "pickaxe_head_cast", new Transform(2.0F * pixel, -1.0F * pixel, -1.0F * pixel, 0.0F, 1.0F));
        putPair(defaults, "axe_head_part", "axe_head_cast", new Transform(3.0F * pixel, 2.0F * pixel, -1.0F * pixel, 90.0F, 1.0F));
        putPair(defaults, "shovel_head_part", "shovel_head_cast", new Transform(3.0F * pixel, -3.0F * pixel, -1.0F * pixel, 0.0F, 1.0F));
        putPair(defaults, "sword_head_part", "sword_head_cast", new Transform(2.0F * pixel, -2.0F * pixel, -1.0F * pixel, 0.0F, 1.0F));
        putPair(defaults, "tool_binding_part", "tool_binding_cast", new Transform(1.0F * pixel, 0.0F, -1.0F * pixel, 90.0F, 1.0F));
        putPair(defaults, "tool_handle_part", "tool_handle_cast", new Transform(0.0F, 1.0F * pixel, -1.0F * pixel, 0.0F, 1.0F));
        return defaults;
    }

    private static void putPair(Map<Identifier, Transform> defaults, String partItem, String castItem, Transform transform) {
        defaults.put(VoltaicContent.id(partItem), transform);
        defaults.put(VoltaicContent.id(castItem), transform);
    }

    public record Transform(float x, float y, float z, float rotation, float scale) {
        public static final Transform NONE = new Transform(0.0F, 0.0F, 0.0F, 0.0F, 1.0F);

        private Transform normalized() {
            return scale == 0.0F ? new Transform(x, y, z, rotation, 1.0F) : this;
        }
    }
}
