package com.warpgames.voltaicforgery.voltaic.item;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.energy.VoltaicItemEnergy;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import com.warpgames.voltaicforgery.voltaic.tool.ToolAssembly;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierState;
import com.warpgames.voltaicforgery.voltaic.trait.HasteTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class ModularToolHelper {

    public static void appendTooltip(ItemStack stack, Consumer<Component> tooltip) {
        ToolAssembly assembly = stack.getOrDefault(VoltaicContent.ASSEMBLED_TOOL.get(), ToolAssembly.DEFAULT);
        tooltip.accept(Component.translatable("tooltip.voltaicforgery.tool_parts").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.voltaicforgery.tool_part_head", assembly.headMaterial()).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.accept(Component.translatable("tooltip.voltaicforgery.tool_part_binding", assembly.bindingMaterial()).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.accept(Component.translatable("tooltip.voltaicforgery.tool_part_handle", assembly.handleMaterial()).withStyle(ChatFormatting.DARK_GRAY));

        int slots = stack.getOrDefault(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), 0);
        tooltip.accept(Component.translatable("tooltip.voltaicforgery.modifier_slots", slots).withStyle(slots > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

        ToolEnergyStorageComponent energy = stack.getOrDefault(VoltaicContent.TOOL_ENERGY.get(), VoltaicItemEnergy.EMPTY);
        if (energy.isEnabled()) {
            tooltip.accept(Component.translatable(
                    "tooltip.voltaicforgery.tool_energy",
                    energy.currentEnergy(),
                    energy.maxEnergy()
            ).withStyle(ChatFormatting.AQUA));
        }

        List<String> traits = stack.getOrDefault(VoltaicContent.TOOL_TRAITS.get(), List.of());
        if (!traits.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.voltaicforgery.traits").withStyle(ChatFormatting.GRAY));
            for (String traitId : traits) {
                tooltip.accept(Component.literal(" - ")
                        .append(Component.translatable("trait.voltaicforgery." + traitId))
                        .withStyle(ChatFormatting.AQUA));
            }
        }

        List<ToolModifierState> modifiers = stack.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        if (!modifiers.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.voltaicforgery.modifiers").withStyle(ChatFormatting.GRAY));
            for (ToolModifierState modifier : modifiers) {
                tooltip.accept(Component.literal(" - ")
                        .append(modifierText(modifier))
                        .withStyle(ChatFormatting.BLUE));
            }
        }
    }

    private static Component modifierText(ToolModifierState modifier) {
        Component trait = Component.translatable("trait.voltaicforgery." + modifier.trait());
        Component progress = progressText(modifier);
        if ("haste".equals(modifier.trait())) {
            int percent = Math.round((HasteTrait.multiplier(modifier) - 1.0F) * 100.0F);
            return Component.translatable("tooltip.voltaicforgery.modifier_haste", trait, modifier.startedTier(), progress, percent);
        }
        return Component.translatable("tooltip.voltaicforgery.modifier", trait, modifier.startedTier(), progress);
    }

    private static Component progressText(ToolModifierState modifier) {
        if (modifier.requiredValue() > 0) {
            return Component.literal(modifier.progress() + " / " + modifier.requiredValue());
        }
        return Component.literal(String.valueOf(modifier.progress()));
    }

    public static boolean isBarVisible(ItemStack stack, boolean defaultVisible) {
        ToolEnergyStorageComponent energy = stack.getOrDefault(VoltaicContent.TOOL_ENERGY.get(), VoltaicItemEnergy.EMPTY);
        if (energy.isEnabled()) {
            return energy.currentEnergy() < energy.maxEnergy();
        }
        return defaultVisible;
    }

    public static int getBarWidth(ItemStack stack, int defaultWidth) {
        ToolEnergyStorageComponent energy = stack.getOrDefault(VoltaicContent.TOOL_ENERGY.get(), VoltaicItemEnergy.EMPTY);
        if (energy.isEnabled() && energy.maxEnergy() > 0L) {
            return (int) Math.round(13.0 * (double) energy.currentEnergy() / (double) energy.maxEnergy());
        }
        return defaultWidth;
    }

    public static int getBarColor(ItemStack stack, int defaultColor) {
        ToolEnergyStorageComponent energy = stack.getOrDefault(VoltaicContent.TOOL_ENERGY.get(), VoltaicItemEnergy.EMPTY);
        if (energy.isEnabled()) {
            return 0xFF00FFFF;
        }
        return defaultColor;
    }
}
