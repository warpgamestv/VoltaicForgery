package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.menu.AssemblyStationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AssemblyStationScreen extends AbstractContainerScreen<AssemblyStationMenu> {

    public AssemblyStationScreen(AssemblyStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 220, 190);
        inventoryLabelY = 96;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF2B2B2B);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 92, 0xFF3C3C3C);
        drawSlotFrames(guiGraphics);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private void drawSlotFrames(GuiGraphicsExtractor guiGraphics) {
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 68, 44);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 92, 44);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 116, 44);
        GuiSlotFrames.output(guiGraphics, leftPos, topPos, 166, 44);
        GuiSlotFrames.playerInventory(guiGraphics, leftPos, topPos, 30, 108, 166);

        guiGraphics.fill(leftPos + 142, topPos + 52, leftPos + 154, topPos + 54, 0xFF858585);
        guiGraphics.fill(leftPos + 150, topPos + 49, leftPos + 154, topPos + 57, 0xFF858585);
    }
}

