package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.item.ToolPartItem;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public final class ToolPartColorHandler {

    public static final int UNRESOLVED_COLOR = -1;

    private ToolPartColorHandler() {}

    public static int getTintColor(ItemStack stack, HolderLookup.Provider registries) {
        if (stack == null || stack.isEmpty()) return UNRESOLVED_COLOR;
        String materialId = ToolPartItem.materialId(stack);
        return ToolMaterialResolver.resolveOptional(registries, materialId)
                .map(stat -> 0xFF000000 | stat.tintColor())
                .orElse(UNRESOLVED_COLOR);
    }
}

