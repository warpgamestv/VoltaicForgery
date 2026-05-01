package com.warpgames.voltaicforgery.voltaic.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Common (platform-independent) color lookup for molten metals.
 * Used by block entity renderers to tint fluid streams and tank visuals.
 */
public final class MoltenFluidColors {

    public static final Identifier RELOAD_ID = VoltaicContent.id("molten_fluid_colors");
    private static volatile Map<String, Integer> tints = defaultTints();

    private MoltenFluidColors() {}

    public static void reload(ResourceManager resourceManager) {
        Map<String, Integer> loaded = defaultTints();
        Map<Identifier, Resource> resources = resourceManager.listResources(
                "render/molten_fluid_colors",
                id -> id.getPath().endsWith(".json")
        );

        resources.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> loadResource(loaded, entry.getKey(), entry.getValue()));

        tints = Map.copyOf(loaded);
    }

    private static void loadResource(Map<String, Integer> loaded, Identifier id, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("replace") && root.get("replace").getAsBoolean()) {
                loaded.clear();
            }

            JsonObject entries = root.has("entries") ? root.getAsJsonObject("entries") : root;
            for (Map.Entry<String, JsonElement> entry : entries.entrySet()) {
                if (entry.getKey().equals("replace") || entry.getKey().equals("entries")) continue;
                loaded.put(normalizeFluidKey(entry.getKey()), parseColor(entry.getValue()));
            }
        } catch (Exception e) {
            Constants.LOG.warn("Failed to load molten fluid colors from {}", id, e);
        }
    }

    /**
     * Returns the ARGB tint color for a molten fluid. Defaults to white if unknown.
     */
    public static int tint(Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) return 0xFFFFFFFF;
        Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
        return tints.getOrDefault(normalizeFluidKey(id.toString()), 0xFFFFFFFF);
    }

    private static Map<String, Integer> defaultTints() {
        Map<String, Integer> defaults = new HashMap<>();
        defaults.put("voltaicforgery:molten_copper",   0xFFFF8A3D);
        defaults.put("voltaicforgery:molten_iron",     0xFFD8DEE8);
        defaults.put("voltaicforgery:molten_gold",     0xFFFFD700);
        defaults.put("voltaicforgery:molten_tin",      0xFFC7C7C7);
        defaults.put("voltaicforgery:molten_lead",     0xFF5C6274);
        defaults.put("voltaicforgery:molten_silver",   0xFFD3D3E8);
        defaults.put("voltaicforgery:molten_nickel",   0xFFA8B0A0);
        defaults.put("voltaicforgery:molten_bronze",   0xFFCD7F32);
        defaults.put("voltaicforgery:molten_electrum", 0xFFE8D56A);
        return defaults;
    }

    private static String normalizeFluidKey(String id) {
        Identifier parsed = Identifier.parse(id);
        return parsed.toString();
    }

    private static int parseColor(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String text = element.getAsString().replace("#", "").replace("0x", "").replace("0X", "");
            long value = Long.parseLong(text, 16);
            if (text.length() <= 6) {
                value |= 0xFF000000L;
            }
            return (int) value;
        }
        return element.getAsInt();
    }

    /**
     * Extracts the red component (0-255) from an ARGB color.
     */
    public static int red(int argb) { return (argb >> 16) & 0xFF; }

    /**
     * Extracts the green component (0-255) from an ARGB color.
     */
    public static int green(int argb) { return (argb >> 8) & 0xFF; }

    /**
     * Extracts the blue component (0-255) from an ARGB color.
     */
    public static int blue(int argb) { return argb & 0xFF; }

    /**
     * Extracts the alpha component (0-255) from an ARGB color.
     */
    public static int alpha(int argb) { return (argb >> 24) & 0xFF; }
}
