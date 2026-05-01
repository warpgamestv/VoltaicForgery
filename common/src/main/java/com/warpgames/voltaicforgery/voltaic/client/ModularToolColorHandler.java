package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialResolver;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public final class ModularToolColorHandler {

    public static final int HANDLE_TINT_INDEX = 0;
    public static final int BINDING_TINT_INDEX = 1;
    public static final int HEAD_TINT_INDEX = 2;
    public static final int UNRESOLVED_COLOR = -1;

    private ModularToolColorHandler() {}

    public static int getTintColor(ItemStack stack, HolderLookup.Provider registries, int tintIndex) {
        if (stack == null || stack.isEmpty()) return UNRESOLVED_COLOR;

        ToolAssembly assembly = stack.getOrDefault(VoltaicContent.ASSEMBLED_TOOL.get(), ToolAssembly.DEFAULT);
        String materialId = switch (tintIndex) {
            case HANDLE_TINT_INDEX -> assembly.handleMaterial();
            case BINDING_TINT_INDEX -> assembly.bindingMaterial();
            case HEAD_TINT_INDEX -> assembly.headMaterial();
            default -> null;
        };
        if (materialId == null) return UNRESOLVED_COLOR;

        return ToolMaterialResolver.resolveOptional(registries, materialId)
                .map(stat -> 0xFF000000 | stat.tintColor())
                .orElse(UNRESOLVED_COLOR);
    }
}
