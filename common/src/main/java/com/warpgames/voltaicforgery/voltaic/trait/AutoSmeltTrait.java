package com.warpgames.voltaicforgery.voltaic.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public final class AutoSmeltTrait implements ToolTrait {

    @Override
    public void onBlockBreak(Level level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Vanilla creates item drops after the block-break callback, so transform nearby drops shortly after.
        serverLevel.getServer().execute(() -> smeltNearbyDrops(serverLevel, pos));
    }

    private static void smeltNearbyDrops(ServerLevel level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(1.5D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            ItemStack smelted = smelt(level, item.getItem());
            if (!smelted.isEmpty()) {
                item.setItem(smelted);
            }
        }
    }

    private static ItemStack smelt(ServerLevel level, ItemStack inputStack) {
        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(
                RecipeType.SMELTING,
                input,
                level
        );
        if (recipe.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = recipe.get().value().assemble(input);
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copy = result.copy();
        copy.setCount(result.getCount() * inputStack.getCount());
        return copy;
    }
}
