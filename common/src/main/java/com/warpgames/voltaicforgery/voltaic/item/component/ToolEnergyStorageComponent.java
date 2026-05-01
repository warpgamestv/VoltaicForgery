package com.warpgames.voltaicforgery.voltaic.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Energy stored on an item (FE-style units) as a data component. Immutable; use {@code withX} to update.
 */
public record ToolEnergyStorageComponent(long currentEnergy, long maxEnergy) {
    public static final long DEFAULT_MAX_ENERGY = 50_000L;
    public static final long ENERGY_PER_USE = 100L;

    public ToolEnergyStorageComponent {
        if (maxEnergy < 0) {
            maxEnergy = 0;
        }
        currentEnergy = Math.max(0, Math.min(currentEnergy, maxEnergy));
    }

    public static final Codec<ToolEnergyStorageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("current").forGetter(ToolEnergyStorageComponent::currentEnergy),
            Codec.LONG.fieldOf("max").forGetter(ToolEnergyStorageComponent::maxEnergy)
    ).apply(instance, ToolEnergyStorageComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolEnergyStorageComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ToolEnergyStorageComponent::currentEnergy,
            ByteBufCodecs.VAR_LONG, ToolEnergyStorageComponent::maxEnergy,
            ToolEnergyStorageComponent::new
    );

    public boolean isEnabled() {
        return maxEnergy > 0L;
    }

    public boolean canAffordUse() {
        return currentEnergy > ENERGY_PER_USE;
    }

    public ToolEnergyStorageComponent withCurrent(long value) {
        return new ToolEnergyStorageComponent(value, maxEnergy);
    }

    public ToolEnergyStorageComponent withMax(long value) {
        return new ToolEnergyStorageComponent(currentEnergy, value);
    }
}
