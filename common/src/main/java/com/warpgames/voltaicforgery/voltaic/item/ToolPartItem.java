package com.warpgames.voltaicforgery.voltaic.item;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolMaterialStat;
import com.warpgames.voltaicforgery.voltaic.tool.VoltaicToolMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ToolPartItem extends Item {

    public static final String GENERIC = "generic";
    public static final String PICKAXE_HEAD = "pickaxe_head";
    public static final String AXE_HEAD = "axe_head";
    public static final String SHOVEL_HEAD = "shovel_head";
    public static final String SWORD_HEAD = "sword_head";
    public static final String TOOL_BINDING = "tool_binding";
    public static final String TOOL_HANDLE = "tool_handle";

    private final String partType;

    public ToolPartItem(Properties properties, String materialId) {
        this(properties, materialId, GENERIC);
    }

    public ToolPartItem(Properties properties, String materialId, String partType) {
        // Important: do NOT bake our custom data components into the Item's default components here.
        // NeoForge's registry lifecycle can load recipe JSON (ItemStack.CODEC) before our component
        // types are fully ready, which causes "Item ... does not have components yet" errors.
        //
        // Instead, we attach `tool_part_material` / `tool_part_type` to *ItemStacks* (casts, creative tab,
        // casting output) at runtime / datagen.
        super(properties);
        this.partType = normalizePartType(partType);
    }

    public String partType() {
        return partType;
    }

    public static ItemStack createStack(Item item, String materialId) {
        ItemStack stack = new ItemStack(item);
        stack.set(VoltaicContent.TOOL_PART_MATERIAL.get(), materialId);
        if (item instanceof ToolPartItem toolPartItem) {
            stack.set(VoltaicContent.TOOL_PART_TYPE.get(), toolPartItem.partType());
        }
        return stack;
    }

    public static String materialId(ItemStack stack) {
        return stack.getOrDefault(VoltaicContent.TOOL_PART_MATERIAL.get(), ToolMaterialStat.FALLBACK.materialId());
    }

    public static String partType(ItemStack stack) {
        if (stack.has(VoltaicContent.TOOL_PART_TYPE.get())) {
            return stack.get(VoltaicContent.TOOL_PART_TYPE.get());
        }
        if (stack.getItem() instanceof ToolPartItem toolPartItem) {
            return toolPartItem.partType();
        }
        return GENERIC;
    }

    public static boolean isPartType(ItemStack stack, String partType) {
        return stack.getItem() instanceof ToolPartItem && normalizePartType(partType).equals(partType(stack));
    }

    public int getTintColor(ItemStack stack) {
        return ToolMaterialStat.FALLBACK.tintColor();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
        tooltip.accept(Component.literal("Material: " + materialId(stack)).withStyle(ChatFormatting.GRAY));
        String type = partType(stack);
        if (type != null && !type.isBlank()) {
            tooltip.accept(Component.literal("Part: " + type).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static String normalizePartType(String partType) {
        return partType == null || partType.isBlank() ? GENERIC : partType.toLowerCase(java.util.Locale.ROOT);
    }
}

