package com.warpgames.voltaicforgery.voltaic.block;

import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CrucibleBricksBlock extends Block {
    public CrucibleBricksBlock() {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, VoltaicContent.id("crucible_bricks")))
                .mapColor(MapColor.STONE)
                .strength(2.0F, 6.0F)
                .requiresCorrectToolForDrops()
        );
    }
}

