package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.menu.SolidFuelDynamoMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolidFuelDynamoScreen extends AbstractContainerScreen<SolidFuelDynamoMenu> {

    public SolidFuelDynamoScreen(SolidFuelDynamoMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 220, 190);
        inventoryLabelY = 96;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawPanel(guiGraphics);
        drawEnergyBar(guiGraphics, mouseX, mouseY);
        drawBurnBar(guiGraphics, mouseX, mouseY);
        drawSlotFrames(guiGraphics);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private void drawPanel(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF2B2B2B);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 92, 0xFF3C3C3C);
    }

    private void drawSlotFrames(GuiGraphicsExtractor guiGraphics) {
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 82, 44);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 138, 44);
        GuiSlotFrames.playerInventory(guiGraphics, leftPos, topPos, 30, 108, 166);

        guiGraphics.fill(leftPos + 106, topPos + 52, leftPos + 122, topPos + 54, 0xFF858585);
        guiGraphics.fill(leftPos + 118, topPos + 49, leftPos + 122, topPos + 57, 0xFF858585);
    }

    private void drawEnergyBar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 28;
        int y = topPos + 28;
        int width = 12;
        int height = 54;

        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF111111);
        guiGraphics.fill(x, y, x + width, y + height, 0xFF1C1C1C);

        int max = Math.max(1, menu.getMaxEnergyStored());
        int filled = Math.min(height, menu.getEnergyStored() * height / max);
        guiGraphics.fill(x, y + height - filled, x + width, y + height, 0xFF2D8FEF);

        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal(menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE"), mouseX, mouseY);
        }
    }

    private void drawBurnBar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = leftPos + 70;
        int y = topPos + 72;
        int width = 88;
        int height = 5;

        guiGraphics.fill(x, y, x + width, y + height, 0xFF181818);
        int total = menu.getBurnTimeTotal();
        if (total > 0) {
            int filled = Math.min(width, menu.getBurnTime() * width / total);
            guiGraphics.fill(x, y, x + filled, y + height, 0xFFFF8A3D);
        }
        if (mouseOver(mouseX, mouseY, x, y, width, height)) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal("Burn " + menu.getBurnTime() + " / " + Math.max(1, total)), mouseX, mouseY);
        }
    }

    private static boolean mouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
