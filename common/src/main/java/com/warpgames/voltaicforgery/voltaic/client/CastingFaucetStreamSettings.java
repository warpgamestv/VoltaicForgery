package com.warpgames.voltaicforgery.voltaic.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.warpgames.voltaicforgery.Constants;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.util.Map;

public final class CastingFaucetStreamSettings {

    public static final Identifier RELOAD_ID = VoltaicContent.id("casting_faucet_stream_settings");
    private static final Gson GSON = new Gson();
    private static volatile Settings settings = Settings.DEFAULT;

    private CastingFaucetStreamSettings() {}

    public static Settings get() {
        return settings;
    }

    public static void reload(ResourceManager resourceManager) {
        Settings loaded = Settings.DEFAULT;
        Map<Identifier, Resource> resources = resourceManager.listResources(
                "render/casting_faucet_stream",
                id -> id.getPath().endsWith(".json")
        );

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                loaded = GSON.fromJson(root, Settings.class).normalized();
            } catch (Exception e) {
                Constants.LOG.warn("Failed to load casting faucet stream settings from {}", entry.getKey(), e);
            }
        }

        settings = loaded;
    }

    public record Settings(float width, float top, float bottom, float centerX, float centerZ) {
        public static final Settings DEFAULT = new Settings(2.0F / 16.0F, 4.0F / 16.0F, -12.0F / 16.0F, 0.5F, 0.5F);

        private Settings normalized() {
            float safeWidth = width <= 0.0F ? DEFAULT.width : width;
            return new Settings(safeWidth, top, bottom, centerX, centerZ);
        }
    }
}
