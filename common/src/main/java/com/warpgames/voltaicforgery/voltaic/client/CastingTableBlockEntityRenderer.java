package com.warpgames.voltaicforgery.voltaic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.warpgames.voltaicforgery.voltaic.block.CastingTableBlock;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingTableBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public final class CastingTableBlockEntityRenderer implements BlockEntityRenderer<CastingTableBlockEntity, CastingTableBlockEntityRenderer.State> {

    // Tuning: offsets in block space
    private static final float OFFSET_Y = -0.0625F;
    private static final float FLUID_MIN = 2.0F / 16.0F;
    private static final float FLUID_MAX = 14.0F / 16.0F;
    private static final float FLUID_Y = 15.05F / 16.0F;

    public CastingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CastingTableBlockEntity be, State state, float partialTick, Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(be, state, crumblingOverlay);
        state.cast.clear();
        state.output.clear();

        if (be.getLevel() == null) return;

        if (be.getBlockState().hasProperty(CastingTableBlock.FACING)) {
            state.facing = be.getBlockState().getValue(CastingTableBlock.FACING);
        }

        Fluid fluid = be.getTank().getFluid();
        int amountMb = be.getTank().getAmountMb();
        state.fluidTint = fluid != null && fluid != Fluids.EMPTY && amountMb > 0 ? MoltenFluidColors.tint(fluid) : 0;
        state.fluidFill = 0.0F;

        // Check for output item first
        ItemStack outputStack = be.getItem(CastingTableBlockEntity.SLOT_OUTPUT);
        state.outputItemId = BuiltInRegistries.ITEM.getKey(outputStack.getItem());
        if (!outputStack.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    state.output,
                    outputStack,
                    ItemDisplayContext.FIXED,
                    be.getLevel(),
                    null,
                    0
            );
        }

        // Always show the cast mold if present.
        ItemStack castStack = be.getItem(CastingTableBlockEntity.SLOT_CAST);
        if (state.fluidTint != 0) {
            int requiredMb = be.getMatchedRecipeAmountMb();
            if (requiredMb <= 0) {
                requiredMb = be.getTank().getCapacityMb();
            }
            state.fluidFill = Math.max(0.0F, Math.min(1.0F, (float) amountMb / (float) Math.max(1, requiredMb)));
        }
        if (!castStack.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    state.cast,
                    castStack,
                    ItemDisplayContext.FIXED,
                    be.getLevel(),
                    null,
                    0
            );
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluidTint != 0 && state.fluidFill > 0.0F) {
            renderFluid(state, poseStack, collector);
        }
        if (!state.cast.isEmpty()) {
            renderItem(state.cast, poseStack, collector, state, 0.001F, 0.0F);
        }
        if (!state.output.isEmpty()) {
            // Render the output slightly "higher" (more negative Z after X-rotation) so it sits visually on top of the cast
            renderItem(state.output, poseStack, collector, state, -0.001F, 90.0F);
        }
    }

    private void renderItem(ItemStackRenderState itemState, PoseStack poseStack, SubmitNodeCollector collector, State state, float zOffset, float extraYRot) {
        poseStack.pushPose();
        // Center on the table
        poseStack.translate(0.5F, 1.001F + OFFSET_Y, 0.5F);

        // Rotate based on the block's facing direction + any extra rotation for the specific item
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot() + extraYRot));

        // Lay the item flat (front facing UP)
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        // 14x14 in a 16x16 item space
        float scale = 14.0F / 16.0F;
        poseStack.scale(scale, scale, scale);

        if (itemState == state.output) {
            CastingTablePartTransforms.Transform transform = CastingTablePartTransforms.get(state.outputItemId);
            poseStack.translate(transform.x(), transform.y(), transform.z());
            poseStack.mulPose(Axis.ZP.rotationDegrees(transform.rotation()));
            poseStack.scale(transform.scale(), transform.scale(), transform.scale());
        }

        // Apply a tiny Z-offset to prevent z-fighting. Negative Z is visually "up" because of the X rotation.
        poseStack.translate(0.0F, 0.0F, zOffset);

        itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private static void renderFluid(State state, PoseStack poseStack, SubmitNodeCollector collector) {
        final int tint = state.fluidTint;
        final float fillSize = (FLUID_MAX - FLUID_MIN) * (float) Math.sqrt(state.fluidFill);
        final float min = 0.5F - fillSize / 2.0F;
        final float max = 0.5F + fillSize / 2.0F;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buffer) -> {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getAtlasManager().getAtlasOrThrow(Identifier.fromNamespaceAndPath("minecraft", "blocks"))
                    .getSprite(Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_still"));

            int r = MoltenFluidColors.red(tint);
            int g = MoltenFluidColors.green(tint);
            int b = MoltenFluidColors.blue(tint);
            int a = Math.min(235, MoltenFluidColors.alpha(tint));
            int light = 0x00F000F0;

            vtx(buffer, pose, min, FLUID_Y, min, sprite.getU(0.0F), sprite.getV(0.0F), r, g, b, a, light);
            vtx(buffer, pose, min, FLUID_Y, max, sprite.getU(0.0F), sprite.getV(1.0F), r, g, b, a, light);
            vtx(buffer, pose, max, FLUID_Y, max, sprite.getU(1.0F), sprite.getV(1.0F), r, g, b, a, light);
            vtx(buffer, pose, max, FLUID_Y, min, sprite.getU(1.0F), sprite.getV(0.0F), r, g, b, a, light);
        });
    }

    private static void vtx(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int r, int g, int b, int a, int light) {
        buffer.addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public static final class State extends BlockEntityRenderState {
        public final ItemStackRenderState cast = new ItemStackRenderState();
        public final ItemStackRenderState output = new ItemStackRenderState();
        public Direction facing = Direction.NORTH;
        public net.minecraft.resources.Identifier outputItemId = net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "air");
        public int fluidTint = 0;
        public float fluidFill = 0.0F;
    }
}
