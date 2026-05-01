package com.warpgames.voltaicforgery.voltaic.tool;

public final class ToolDurabilityResolver {

    private ToolDurabilityResolver() {
    }

    public static int calculate(ToolMaterialStat head, ToolMaterialStat binding, ToolMaterialStat handle) {
        float durability = (head.durability() * head.headDurabilityMultiplier())
                + (binding.durability() * binding.bindingDurabilityMultiplier())
                + (handle.durability() * handle.handleDurabilityMultiplier());
        return Math.max(1, Math.round(durability));
    }
}
