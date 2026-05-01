package com.warpgames.voltaicforgery.voltaic.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

final class GuiSlotFrames {

    private static final int OUTER = 0xFF111111;
    private static final int INNER = 0xFF555555;
    private static final int FILL = 0xFF191919;
    private static final int OUTPUT_INNER = 0xFFE0B84E;

    private GuiSlotFrames() {
    }

    static void input(GuiGraphicsExtractor guiGraphics, int left, int top, int slotX, int slotY) {
        frame(guiGraphics, left + slotX - 1, top + slotY - 1, INNER);
    }

    static void output(GuiGraphicsExtractor guiGraphics, int left, int top, int slotX, int slotY) {
        frame(guiGraphics, left + slotX - 1, top + slotY - 1, OUTPUT_INNER);
    }

    static void playerInventory(GuiGraphicsExtractor guiGraphics, int left, int top, int inventoryX, int inventoryY, int hotbarY) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                neutral(guiGraphics, left, top, inventoryX + column * 18, inventoryY + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            neutral(guiGraphics, left, top, inventoryX + column * 18, hotbarY);
        }
    }

    static void neutral(GuiGraphicsExtractor guiGraphics, int left, int top, int slotX, int slotY) {
        frame(guiGraphics, left + slotX - 1, top + slotY - 1, 0xFF3F3F3F);
    }

    private static void frame(GuiGraphicsExtractor guiGraphics, int x, int y, int accent) {
        guiGraphics.fill(x, y, x + 18, y + 18, OUTER);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, accent);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 16, FILL);
    }
}
