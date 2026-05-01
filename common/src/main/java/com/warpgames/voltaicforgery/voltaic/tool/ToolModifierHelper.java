package com.warpgames.voltaicforgery.voltaic.tool;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.item.ModularAxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularShovelItem;
import com.warpgames.voltaicforgery.voltaic.item.ModularSwordItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class ToolModifierHelper {

    private ToolModifierHelper() {
    }

    public static int activeLevel(ItemStack stack, String trait) {
        return state(stack, trait)
                .map(ToolModifierState::activeLevel)
                .orElse(0);
    }

    public static Optional<ToolModifierState> state(ItemStack stack, String trait) {
        List<ToolModifierState> modifiers = stack.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        return ToolModifierState.find(modifiers, trait);
    }

    public static String toolType(ItemStack tool) {
        if (tool.getItem() instanceof ModularPickaxeItem) {
            return "pickaxe";
        }
        if (tool.getItem() instanceof ModularAxeItem) {
            return "axe";
        }
        if (tool.getItem() instanceof ModularShovelItem) {
            return "shovel";
        }
        if (tool.getItem() instanceof ModularSwordItem) {
            return "sword";
        }
        return "";
    }
}
