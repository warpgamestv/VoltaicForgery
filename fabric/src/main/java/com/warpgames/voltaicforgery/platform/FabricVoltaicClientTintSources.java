package com.warpgames.voltaicforgery.platform;

import com.mojang.serialization.MapCodec;
import com.warpgames.voltaicforgery.voltaic.client.ToolPartTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Field;

public final class FabricVoltaicClientTintSources {

    private FabricVoltaicClientTintSources() {}

    public static void register() {
        register(com.warpgames.voltaicforgery.voltaic.client.ModularToolTintSource.ID, com.warpgames.voltaicforgery.voltaic.client.ModularToolTintSource.MAP_CODEC);
        register(ToolPartTintSource.ID, ToolPartTintSource.MAP_CODEC);
    }

    private static void register(Identifier id, MapCodec<? extends ItemTintSource> codec) {
        try {
            Field mapperField = ItemTintSources.class.getDeclaredField("ID_MAPPER");
            mapperField.setAccessible(true);
            Object mapper = mapperField.get(null);
            mapper.getClass().getMethod("put", Object.class, Object.class).invoke(mapper, id, codec);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register Fabric item tint source " + id, e);
        }
    }
}
