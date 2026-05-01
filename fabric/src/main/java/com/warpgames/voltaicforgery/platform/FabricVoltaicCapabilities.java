package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.platform.services.IVoltaicCapabilities;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingTableBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.InductionCrucibleBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

/**
 * Mod fluid exposure for machines plus optional item energy interop bridge.
 */
public class FabricVoltaicCapabilities implements IVoltaicCapabilities {

    @Override
    public void init() {
        FluidStorage.SIDED.registerForBlockEntity(
                (InductionCrucibleBlockEntity be, net.minecraft.core.Direction dir) -> new FabricWrappedFluidStorage(be.getTank()),
                VoltaicContent.INDUCTION_CRUCIBLE_BE.get()
        );
        FluidStorage.SIDED.registerForBlockEntity(
                (CastingTableBlockEntity be, net.minecraft.core.Direction dir) -> new FabricWrappedFluidStorage(be.getTank()),
                VoltaicContent.CASTING_TABLE_BE.get()
        );
    }

    @Override
    public long receiveEnergyToItem(ItemStack stack, long maxReceive, boolean simulate) {
        if (stack == null || stack.isEmpty() || maxReceive <= 0L) {
            return 0L;
        }
        // Optional bridge: TeamReborn / Tech Reborn Energy API.
        // Reflection keeps this integration optional with no hard dependency.
        boolean hasEnergyApi = FabricLoader.getInstance().isModLoaded("team_reborn_energy")
                || FabricLoader.getInstance().isModLoaded("techreborn");
        if (!hasEnergyApi) {
            return 0L;
        }
        try {
            Class<?> energyStorageClass = Class.forName("team.reborn.energy.api.EnergyStorage");
            Object itemLookup = energyStorageClass.getField("ITEM").get(null);

            Class<?> itemContextClass = Class.forName("net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext");
            Object itemContext;
            try {
                Method withInitial = itemContextClass.getMethod("withInitial", ItemStack.class);
                itemContext = withInitial.invoke(null, stack);
            } catch (NoSuchMethodException ignored) {
                Method withConstant = itemContextClass.getMethod("withConstant", ItemStack.class);
                itemContext = withConstant.invoke(null, stack);
            }

            Method find = itemLookup.getClass().getMethod("find", Object.class, Object.class);
            Object storage = find.invoke(itemLookup, stack, itemContext);
            if (storage == null) {
                return 0L;
            }

            Class<?> transactionClass = Class.forName("net.fabricmc.fabric.api.transfer.v1.transaction.Transaction");
            Method openOuter = transactionClass.getMethod("openOuter");
            Object tx = openOuter.invoke(null);
            try {
                Method insert = storage.getClass().getMethod("insert", long.class, transactionClass);
                long inserted = (long) insert.invoke(storage, maxReceive, tx);
                if (inserted > 0L && !simulate) {
                    Method commit = tx.getClass().getMethod("commit");
                    commit.invoke(tx);
                }
                return Math.max(0L, inserted);
            } finally {
                Method close = tx.getClass().getMethod("close");
                close.invoke(tx);
            }
        } catch (Throwable ignored) {
            return 0L;
        }
    }
}

