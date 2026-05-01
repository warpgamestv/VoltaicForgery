package com.warpgames.voltaicforgery.voltaic.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.voltaicforgery.voltaic.VoltaicContent;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.Direction;

public class CastingTableBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public static final MapCodec<CastingTableBlock> CODEC = MapCodec.unit(CastingTableBlock::new);

    public CastingTableBlock() {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, VoltaicContent.id("casting_table")))
                .strength(3.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CastingTableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == VoltaicContent.CASTING_TABLE_BE.get()
                ? (lvl, p, s, be) -> CastingTableBlockEntity.serverTick(lvl, p, s, (CastingTableBlockEntity) be)
                : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CastingTableBlockEntity be)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack held = player.getMainHandItem();

        // Insert a casting input (reusable cast or sacrificial mold part) if the slot is empty.
        if (!held.isEmpty() && isCastingInput(held) && be.getItem(CastingTableBlockEntity.SLOT_CAST).isEmpty()) {
            ItemStack one = held.copy();
            one.setCount(1);
            be.setItem(CastingTableBlockEntity.SLOT_CAST, one);
            held.shrink(1);
            be.setChanged();
            return InteractionResult.SUCCESS;
        }

        // If empty hand, take output first, otherwise take cast.
        if (held.isEmpty()) {
            ItemStack out = be.getItem(CastingTableBlockEntity.SLOT_OUTPUT);
            if (!out.isEmpty()) {
                be.setItem(CastingTableBlockEntity.SLOT_OUTPUT, ItemStack.EMPTY);
                giveToPlayer(player, out);
                be.setChanged();
                return InteractionResult.SUCCESS;
            }

            ItemStack cast = be.getItem(CastingTableBlockEntity.SLOT_CAST);
            if (!cast.isEmpty() && be.getCoolingTime() <= 0) {
                be.setItem(CastingTableBlockEntity.SLOT_CAST, ItemStack.EMPTY);
                giveToPlayer(player, cast);
                be.setChanged();
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    /** Tag-driven cast check — addon mods can add items to #voltaicforgery:casts to make them work here. */
    public static final TagKey<Item> CASTS_TAG = TagKey.create(Registries.ITEM, VoltaicContent.id("casts"));
    public static final TagKey<Item> CASTING_INPUTS_TAG = TagKey.create(Registries.ITEM, VoltaicContent.id("casting_inputs"));

    private static boolean isCastingInput(ItemStack stack) {
        return stack.is(CASTS_TAG) || stack.is(CASTING_INPUTS_TAG);
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        if (player instanceof ServerPlayer sp) {
            sp.containerMenu.broadcastChanges();
        }
    }
}
