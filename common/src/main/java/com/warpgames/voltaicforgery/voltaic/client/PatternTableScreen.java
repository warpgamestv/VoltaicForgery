package com.warpgames.voltaicforgery.voltaic.client;

import com.warpgames.voltaicforgery.voltaic.blockentity.PatternTableBlockEntity;
import com.warpgames.voltaicforgery.voltaic.menu.PatternTableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PatternTableScreen extends AbstractContainerScreen<PatternTableMenu> {

    private final List<Button> partButtons = new ArrayList<>();

    public PatternTableScreen(PatternTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 206, 186);
        inventoryLabelY = 93;
    }

    @Override
    protected void init() {
        super.init();
        partButtons.clear();
        List<PatternTableBlockEntity.PartChoice> choices = PatternTableBlockEntity.partChoices(recipeManager());
        for (int i = 0; i < choices.size(); i++) {
            final int part = i;
            ItemStack icon = new ItemStack(BuiltInRegistries.ITEM.getValue(choices.get(i).icon()));
            if (icon.isEmpty() || icon.is(Items.AIR)) {
                icon = new ItemStack(Items.PAPER);
            }
            Button button = new IconButton(leftPos + 10 + (i % 3) * 24, topPos + 18 + (i / 3) * 24, icon, clicked -> {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, part);
                }
            });
            partButtons.add(addRenderableWidget(button));
        }
    }

    private RecipeManager recipeManager() {
        if (minecraft == null) return null;
        if (minecraft.getSingleplayerServer() != null) {
            return minecraft.getSingleplayerServer().getRecipeManager();
        }
        RecipeManager fromLevel = reflectRecipeManager(minecraft.level);
        if (fromLevel != null) return fromLevel;
        return reflectRecipeManager(minecraft.getConnection());
    }

    private static RecipeManager reflectRecipeManager(Object holder) {
        if (holder == null) return null;
        for (String name : List.of("getRecipeManager", "recipeManager", "recipes")) {
            try {
                Method method = holder.getClass().getMethod(name);
                Object result = method.invoke(holder);
                if (result instanceof RecipeManager recipeManager) {
                    return recipeManager;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        for (Method method : holder.getClass().getMethods()) {
            if (method.getParameterCount() != 0 || !RecipeManager.class.isAssignableFrom(method.getReturnType())) continue;
            try {
                Object result = method.invoke(holder);
                if (result instanceof RecipeManager recipeManager) {
                    return recipeManager;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF232323);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 84, 0xFF343434);
        drawSlotFrames(guiGraphics);
        guiGraphics.fill(leftPos + 109, topPos + 42, leftPos + 117, topPos + 44, 0xFF858585);
        guiGraphics.fill(leftPos + 145, topPos + 42, leftPos + 160, topPos + 44, 0xFF858585);
        guiGraphics.fill(leftPos + 156, topPos + 39, leftPos + 160, topPos + 47, 0xFF858585);

        int selected = menu.getSelectedPart();
        for (int i = 0; i < partButtons.size(); i++) {
            Button button = partButtons.get(i);
            button.active = i != selected;
            if (i == selected) {
                guiGraphics.fill(button.getX() - 2, button.getY() - 2, button.getX() + button.getWidth() + 2, button.getY(), 0xFFE0B84E);
                guiGraphics.fill(button.getX() - 2, button.getY() + button.getHeight(), button.getX() + button.getWidth() + 2, button.getY() + button.getHeight() + 2, 0xFFE0B84E);
                guiGraphics.fill(button.getX() - 2, button.getY(), button.getX(), button.getY() + button.getHeight(), 0xFFE0B84E);
                guiGraphics.fill(button.getX() + button.getWidth(), button.getY(), button.getX() + button.getWidth() + 2, button.getY() + button.getHeight(), 0xFFE0B84E);
            }
        }

        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawSlotFrames(GuiGraphicsExtractor guiGraphics) {
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 89, 35);
        GuiSlotFrames.input(guiGraphics, leftPos, topPos, 123, 35);
        GuiSlotFrames.output(guiGraphics, leftPos, topPos, 169, 35);
        GuiSlotFrames.playerInventory(guiGraphics, leftPos, topPos, 22, 104, 162);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFFFFFFF, false);
    }

    private static final class IconButton extends Button {
        private final ItemStack icon;

        private IconButton(int x, int y, ItemStack icon, OnPress onPress) {
            super(x, y, 22, 22, Component.empty(), onPress, DEFAULT_NARRATION);
            this.icon = icon;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            extractDefaultSprite(guiGraphics);
            guiGraphics.fakeItem(icon, getX() + 3, getY() + 3);
        }
    }
}
