package com.warpgames.voltaicforgery.voltaic.tool;

public final class VoltaicToolMaterials {

    private VoltaicToolMaterials() {}

    public static String normalize(String materialId) {
        if (materialId == null || materialId.isBlank()) return ToolMaterialStat.FALLBACK.materialId();
        return materialId.toLowerCase(java.util.Locale.ROOT);
    }
}

