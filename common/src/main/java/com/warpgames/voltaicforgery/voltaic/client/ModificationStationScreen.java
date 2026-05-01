package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.menu.ModificationStationMenu;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierEntry;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierHelper;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierQuery;
import com.warpgames.voltaicforgery.voltaic.tool.ToolModifierState;
import com.warpgames.voltaicforgery.voltaic.trait.HasteTrait;
import com.warpgames.voltaicforgery.voltaic.trait.ToolTraits;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class ModificationStationScreen extends AbstractContainerScreen<ModificationStationMenu> {

    private static final int DEFAULT_MODIFIER_SLOTS = 3;

    public ModificationStationScreen(ModificationStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 220, 190);
        inventoryLabelY = 96;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF2B2B2B);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 92, 0xFF3C3C3C);
        drawSlotFrames(guiGraphics);
        drawModifierInfo(guiGraphics, mouseX, mouseY);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawSlotFrames(GuiGraphicsExtractor guiGraphics) {
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 48, 44);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 100, 44);
        GuiSlotFrames.output(guiGraphics, leftPos, topPos, 154, 44);
        GuiSlotFrames.playerInventory(guiGraphics, leftPos, topPos, 30, 108, 166);
        guiGraphics.fill(leftPos + 72, topPos + 52, leftPos + 88, topPos + 54, 0xFF858585);
        guiGraphics.fill(leftPos + 124, topPos + 52, leftPos + 140, topPos + 54, 0xFF858585);
        guiGraphics.fill(leftPos + 136, topPos + 49, leftPos + 140, topPos + 57, 0xFF858585);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private void drawModifierInfo(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        ItemStack tool = menu.getToolStack();
        ItemStack output = menu.getOutputStack();
        ItemStack display = output.isEmpty() ? tool : output;
        if (display.isEmpty()) {
            guiGraphics.text(font, Component.translatable("gui.voltaicforgery.modification_station.insert_tool"), leftPos + 30, topPos + 72, 0xFFAAAAAA, false);
            return;
        }

        int remaining = display.getOrDefault(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), DEFAULT_MODIFIER_SLOTS);
        drawSlotPips(guiGraphics, leftPos + 30, topPos + 74, remaining);

        Optional<ToolModifierState> changed = changedModifier(tool, output);
        if (output.isEmpty() && !menu.getInputStack().isEmpty()) {
            guiGraphics.text(font, blockedText(tool, menu.getInputStack(), output), leftPos + 96, topPos + 74, 0xFFFF8080, false);
            return;
        }
        Optional<ToolModifierState> shown = changed.or(() -> firstModifier(display));
        if (shown.isEmpty()) {
            guiGraphics.text(font, blockedText(tool, menu.getInputStack(), output), leftPos + 96, topPos + 74, 0xFFAAAAAA, false);
            return;
        }

        ToolModifierState state = shown.get();
        int textColor = output.isEmpty() ? 0xFFAAAAAA : 0xFFE0B84E;
        guiGraphics.text(font, modifierText(state), leftPos + 96, topPos + 72, textColor, false);
        drawProgressBar(guiGraphics, state, mouseX, mouseY);
    }

    private void drawSlotPips(GuiGraphicsExtractor guiGraphics, int x, int y, int remaining) {
        guiGraphics.text(font, Component.translatable("gui.voltaicforgery.modification_station.slots"), x, y - 10, 0xFFAAAAAA, false);
        for (int i = 0; i < DEFAULT_MODIFIER_SLOTS; i++) {
            int color = i < remaining ? 0xFFE0B84E : 0xFF1A1A1A;
            guiGraphics.fill(x + (i * 14), y, x + 10 + (i * 14), y + 10, color);
            guiGraphics.fill(x + 1 + (i * 14), y + 1, x + 9 + (i * 14), y + 9, i < remaining ? 0xFFB98E27 : 0xFF303030);
        }
    }

    private void drawProgressBar(GuiGraphicsExtractor guiGraphics, ToolModifierState state, int mouseX, int mouseY) {
        if (state.requiredValue() <= 0 || state.startedTier() <= 0) {
            return;
        }
        int x = leftPos + 96;
        int y = topPos + 84;
        int width = 94;
        int height = 5;
        int filled = Math.round(width * state.progressFraction());
        guiGraphics.fill(x, y, x + width, y + height, 0xFF181818);
        guiGraphics.fill(x, y, x + filled, y + height, 0xFFE0B84E);
        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal(state.progress() + " / " + state.requiredValue()), mouseX, mouseY);
        }
    }

    private Component modifierText(ToolModifierState state) {
        Component trait = Component.translatable("trait.voltaicforgery." + state.trait());
        if ("haste".equals(state.trait())) {
            int percent = Math.round((HasteTrait.multiplier(state) - 1.0F) * 100.0F);
            return Component.translatable("gui.voltaicforgery.modification_station.modifier_haste", trait, state.startedTier(), percent);
        }
        return Component.translatable("gui.voltaicforgery.modification_station.modifier", trait, state.startedTier());
    }

    private Optional<ToolModifierState> changedModifier(ItemStack before, ItemStack after) {
        if (before.isEmpty() || after.isEmpty()) {
            return Optional.empty();
        }
        List<ToolModifierState> beforeStates = before.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        List<ToolModifierState> afterStates = after.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of());
        for (ToolModifierState afterState : afterStates) {
            Optional<ToolModifierState> beforeState = ToolModifierState.find(beforeStates, afterState.trait());
            if (beforeState.isEmpty()
                    || beforeState.get().progress() != afterState.progress()
                    || beforeState.get().startedTier() != afterState.startedTier()
                    || beforeState.get().activeLevel() != afterState.activeLevel()) {
                return Optional.of(afterState);
            }
        }
        return Optional.empty();
    }

    private Optional<ToolModifierState> firstModifier(ItemStack stack) {
        return stack.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.<ToolModifierState>of()).stream().findFirst();
    }

    private Component blockedText(ItemStack tool, ItemStack input, ItemStack output) {
        if (tool.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.insert_tool");
        }
        if (input.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.insert_material");
        }
        if (!output.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.no_modifiers");
        }
        if (minecraft == null || minecraft.level == null) {
            return Component.translatable("gui.voltaicforgery.modification_station.blocked");
        }

        HolderLookup.Provider registries = minecraft.level.registryAccess();
        Optional<ToolModifierQuery.Match> match = ToolModifierQuery.findForStack(registries, input);
        if (match.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.invalid_material");
        }

        ToolModifierEntry modifier = match.get().entry();
        if (!modifier.allowsTool(ToolModifierHelper.toolType(tool))) {
            return Component.translatable("gui.voltaicforgery.modification_station.wrong_tool");
        }

        List<String> traits = ToolTraits.normalizeIds(List.of(modifier.trait()));
        if (traits.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.blocked");
        }

        ToolModifierState state = ToolModifierState.find(
                tool.getOrDefault(VoltaicContent.TOOL_MODIFIERS.get(), List.of()),
                traits.get(0)
        ).orElse(new ToolModifierState(traits.get(0), 0, 0, 0));
        boolean startingTier = !state.hasStartedTier();
        int tierLevel = startingTier ? state.activeLevel() + 1 : state.startedTier();
        Optional<ToolModifierEntry.Tier> tier = modifier.tier(tierLevel);
        if (tier.isEmpty()) {
            return Component.translatable("gui.voltaicforgery.modification_station.max_tier");
        }

        int remaining = tool.getOrDefault(VoltaicContent.TOOL_MODIFIER_SLOTS.get(), DEFAULT_MODIFIER_SLOTS);
        if (startingTier && tier.get().slotCost() > remaining) {
            return Component.translatable("gui.voltaicforgery.modification_station.no_slots");
        }
        return Component.translatable("gui.voltaicforgery.modification_station.blocked");
    }
}
