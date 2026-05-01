package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.blockentity.InductionCrucibleBlockEntity;
import com.warpgames.voltaicforgery.voltaic.menu.InductionCrucibleMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;

public class InductionCrucibleScreen extends AbstractContainerScreen<InductionCrucibleMenu> {

    public InductionCrucibleScreen(InductionCrucibleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 220, 190);
        inventoryLabelY = 96;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawPanel(guiGraphics);
        drawEnergyBar(guiGraphics, mouseX, mouseY);
        drawHeatBar(guiGraphics, mouseX, mouseY);
        drawFluidTank(guiGraphics, mouseX, mouseY);
        drawProgressBar(guiGraphics, mouseX, mouseY);
        drawSlotFrames(guiGraphics);
        drawStatusText(guiGraphics);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawSlotFrames(GuiGraphicsExtractor guiGraphics) {
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 95, 44);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 119, 44);
        GuiSlotFrames.playerInventory(guiGraphics, leftPos, topPos, 30, 108, 166);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private void drawPanel(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF2B2B2B);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + imageHeight - 4, 0xFF3C3C3C);
    }

    private void drawEnergyBar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 20;
        int y = topPos + 28;
        int width = 12;
        int height = 54;

        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF111111);
        guiGraphics.fill(x, y, x + width, y + height, 0xFF1C1C1C);

        int maxEnergy = Math.max(1, menu.getMaxEnergyStored());
        int filled = Math.min(height, menu.getEnergyStored() * height / maxEnergy);
        guiGraphics.fill(x, y + height - filled, x + width, y + height, 0xFF2D8FEF);
        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE"), mouseX, mouseY);
        }
    }

    private void drawHeatBar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 45;
        int y = topPos + 28;
        int width = 12;
        int height = 54;

        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF111111);
        guiGraphics.fill(x, y, x + width, y + height, 0xFF1C1C1C);

        int maxHeat = Math.max(1, menu.getMaxHeat());
        int filled = Math.min(height, menu.getCurrentHeat() * height / maxHeat);
        guiGraphics.fill(x, y + height - filled, x + width, y + height, 0xFFFF6A00);
        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal("Heat " + menu.getCurrentHeat() + " / " + menu.getMaxHeat()), mouseX, mouseY);
        }
    }

    private void drawFluidTank(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 188;
        int y = topPos + 28;
        int width = 16;
        int height = 54;

        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF111111);
        guiGraphics.fill(x, y, x + width, y + height, 0xFF1C1C1C);

        int capacity = Math.max(1, menu.getFluidCapacity());
        int filled = Math.min(height, menu.getFluidAmount() * height / capacity);
        if (menu.getFluid() != Fluids.EMPTY && filled > 0) {
            guiGraphics.fill(x, y + height - filled, x + width, y + height, MoltenFluidColors.tint(menu.getFluid()));
        }
        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal(menu.getFluidAmount() + " / " + menu.getFluidCapacity() + " mB"), mouseX, mouseY);
        }
    }

    private void drawProgressBar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 72;
        int y = topPos + 72;
        int width = 92;
        int height = 5;

        guiGraphics.fill(x, y, x + width, y + height, 0xFF181818);
        int total = menu.getMeltTimeTotal();
        if (total > 0) {
            int filled = Math.min(width, menu.getMeltProgress() * width / total);
            guiGraphics.fill(x, y, x + filled, y + height, 0xFFFFC36A);
        }
        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal("Progress " + menu.getMeltProgress() + " / " + Math.max(1, total)), mouseX, mouseY);
        }
    }

    private void drawStatusText(GuiGraphicsExtractor guiGraphics) {
        String status = switch (menu.getStatusCode()) {
            case InductionCrucibleBlockEntity.STATUS_NO_RECIPE -> "No recipe";
            case InductionCrucibleBlockEntity.STATUS_HEATING -> "Heating";
            case InductionCrucibleBlockEntity.STATUS_MELTING -> "Melting";
            case InductionCrucibleBlockEntity.STATUS_TANK_FULL -> "Tank full";
            case InductionCrucibleBlockEntity.STATUS_NO_POWER -> "No power";
            default -> "Idle";
        };
        guiGraphics.text(font, Component.literal(status), leftPos + 72, topPos + 24, 0xFFB8C0CC, false);
    }

    private static boolean mouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
