package com.warpgames.voltaicforgery.voltaic.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;

/**
 * Maps data-driven material tool types to vanilla tool rules for "correct tool for drops".
 * The material registry owns the tier choice through ToolMaterialStat#toolType().
 */
public final class VanillaToolComponentResolver {

    private VanillaToolComponentResolver() {}

    public enum ToolKind {
        PICKAXE,
        AXE,
        SHOVEL
    }

    public static Tool resolveToolComponent(ToolKind kind, String toolType) {
        Item vanilla = vanillaItem(kind, normalize(toolType));
        ItemStack ref = new ItemStack(vanilla);
        Tool tool = ref.get(DataComponents.TOOL);
        return tool;
    }

    private static Item vanillaItem(ToolKind kind, String mat) {
        return switch (kind) {
            case PICKAXE -> switch (mat) {
                case "wood" -> Items.WOODEN_PICKAXE;
                case "stone", "copper" -> Items.STONE_PICKAXE;
                case "iron" -> Items.IRON_PICKAXE;
                case "gold" -> Items.GOLDEN_PICKAXE;
                case "diamond" -> Items.DIAMOND_PICKAXE;
                case "netherite" -> Items.NETHERITE_PICKAXE;
                default -> Items.STONE_PICKAXE;
            };
            case AXE -> switch (mat) {
                case "wood" -> Items.WOODEN_AXE;
                case "stone", "copper" -> Items.STONE_AXE;
                case "iron" -> Items.IRON_AXE;
                case "gold" -> Items.GOLDEN_AXE;
                case "diamond" -> Items.DIAMOND_AXE;
                case "netherite" -> Items.NETHERITE_AXE;
                default -> Items.STONE_AXE;
            };
            case SHOVEL -> switch (mat) {
                case "wood" -> Items.WOODEN_SHOVEL;
                case "stone", "copper" -> Items.STONE_SHOVEL;
                case "iron" -> Items.IRON_SHOVEL;
                case "gold" -> Items.GOLDEN_SHOVEL;
                case "diamond" -> Items.DIAMOND_SHOVEL;
                case "netherite" -> Items.NETHERITE_SHOVEL;
                default -> Items.STONE_SHOVEL;
            };
        };
    }

    private static String normalize(String id) {
        return id == null ? "" : id.trim().toLowerCase(java.util.Locale.ROOT);
    }
}

