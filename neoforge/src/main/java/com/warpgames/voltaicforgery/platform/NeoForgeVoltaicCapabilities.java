package com.warpgames.voltaicforgery.platform;

import com.warpgames.voltaicforgery.platform.services.IVoltaicCapabilities;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingTableBlockEntity;
import com.warpgames.voltaicforgery.voltaic.item.IModularTool;
import com.warpgames.voltaicforgery.voltaic.blockentity.InductionCrucibleBlockEntity;
import com.warpgames.voltaicforgery.voltaic.blockentity.SolidFuelDynamoBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public class NeoForgeVoltaicCapabilities implements IVoltaicCapabilities {

    static void attach(IEventBus bus) {
        bus.addListener(NeoForgeVoltaicCapabilities::onRegisterCapabilities);
    }

    @Override
    public void init() {
        // NeoForge wiring happens via events; nothing to do here.
    }

    @Override
    public long receiveEnergyToItem(ItemStack stack, long maxReceive, boolean simulate) {
        if (stack == null || stack.isEmpty() || maxReceive <= 0L) {
            return 0L;
        }
        EnergyHandler handler = ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
        if (handler == null) {
            return 0L;
        }
        int request = (int) Math.min((long) Integer.MAX_VALUE, maxReceive);
        if (request <= 0) {
            return 0L;
        }
        // NeoForge EnergyHandler insert is int-based.
        int accepted = handler.insert(request, null);
        return Math.max(0L, (long) accepted);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.Energy.ITEM,
                (itemStack, context) -> itemStack.getItem() instanceof IModularTool
                        ? new NeoForgeModularToolEnergyHandler(itemStack)
                        : null,
                VoltaicContent.MODULAR_PICKAXE.get(),
                VoltaicContent.MODULAR_AXE.get(),
                VoltaicContent.MODULAR_SHOVEL.get(),
                VoltaicContent.MODULAR_SWORD.get()
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VoltaicContent.SOLID_FUEL_DYNAMO_BE.get(),
                (SolidFuelDynamoBlockEntity be, net.minecraft.core.Direction side) -> new NeoForgeWrappedEnergyHandler(be.getEnergy())
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                VoltaicContent.INDUCTION_CRUCIBLE_BE.get(),
                (InductionCrucibleBlockEntity be, net.minecraft.core.Direction side) -> new NeoForgeWrappedEnergyHandler(be.getEnergy())
        );

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                VoltaicContent.INDUCTION_CRUCIBLE_BE.get(),
                (InductionCrucibleBlockEntity be, net.minecraft.core.Direction side) -> new NeoForgeWrappedFluidResourceHandler(be.getTank())
        );

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                VoltaicContent.CASTING_TABLE_BE.get(),
                (CastingTableBlockEntity be, net.minecraft.core.Direction side) -> new NeoForgeWrappedFluidResourceHandler(be.getTank())
        );
    }
}

