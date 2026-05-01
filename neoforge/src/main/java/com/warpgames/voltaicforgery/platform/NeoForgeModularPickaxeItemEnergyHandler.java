package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.api.energy.VoltaicItemEnergy;
import com.warpgames.voltaicforgery.voltaic.item.ModularPickaxeItem;
import com.warpgames.voltaicforgery.voltaic.item.component.ToolEnergyStorageComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes the modular pickaxe's {@code TOOL_ENERGY} component to NeoForge {@code Capabilities.Energy.ITEM} (FE/RF).
 */
public final class NeoForgeModularPickaxeItemEnergyHandler implements EnergyHandler {

    private final ItemStack stack;

    public NeoForgeModularPickaxeItemEnergyHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long getAmountAsLong() {
        if (!isPoweredPickaxeWithEnergy()) {
            return 0L;
        }
        return VoltaicItemEnergy.INSTANCE.getEnergy(stack);
    }

    @Override
    public long getCapacityAsLong() {
        if (!isPoweredPickaxeWithEnergy()) {
            return 0L;
        }
        return VoltaicItemEnergy.INSTANCE.getMaxEnergy(stack);
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (amount <= 0 || !isPoweredPickaxeWithEnergy()) {
            return 0;
        }
        if (amount > Integer.MAX_VALUE) {
            amount = Integer.MAX_VALUE;
        }
        return (int) Math.min(
                (long) Integer.MAX_VALUE,
                VoltaicItemEnergy.INSTANCE.receiveEnergy(stack, amount, false)
        );
    }

    @Override
    public int extract(int amount, TransactionContext context) {
        if (amount <= 0 || !isPoweredPickaxeWithEnergy()) {
            return 0;
        }
        if (amount > Integer.MAX_VALUE) {
            amount = Integer.MAX_VALUE;
        }
        return (int) Math.min(
                (long) Integer.MAX_VALUE,
                VoltaicItemEnergy.INSTANCE.extractEnergy(stack, amount, false)
        );
    }

    private boolean isPoweredPickaxeWithEnergy() {
        if (!(stack.getItem() instanceof ModularPickaxeItem)) {
            return false;
        }
        ToolEnergyStorageComponent c = stack.getOrDefault(
                VoltaicContent.TOOL_ENERGY.get(),
                VoltaicItemEnergy.EMPTY
        );
        return c.isEnabled();
    }
}
