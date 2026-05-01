package com.warpgames.voltaicforgery.voltaic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.warpgames.voltaicforgery.voltaic.blockentity.CastingFaucetBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public final class CastingFaucetBlockEntityRenderer implements BlockEntityRenderer<CastingFaucetBlockEntity, CastingFaucetBlockEntityRenderer.State> {

    public CastingFaucetBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CastingFaucetBlockEntity be, State state, float partialTick, Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(be, state, crumblingOverlay);
        state.isPouring = be.isPouring();
        Fluid fluid = be.getPouringFluid();
        state.fluidTint = (fluid != null && fluid != Fluids.EMPTY) ? MoltenFluidColors.tint(fluid) : 0;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.isPouring || state.fluidTint == 0) return;

        final int tint = state.fluidTint;

        // Submit custom geometry using the new 26.1.2 render submission API.
        // RenderType.translucent() gives us a translucent quad layer.
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buffer) -> {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getAtlasManager().getAtlasOrThrow(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "blocks"))
                    .getSprite(net.minecraft.resources.Identifier.fromNamespaceAndPath("voltaicforgery", "block/molten_metal_flow"));

            int r = MoltenFluidColors.red(tint);
            int g = MoltenFluidColors.green(tint);
            int b = MoltenFluidColors.blue(tint);
            int a = MoltenFluidColors.alpha(tint);
            CastingFaucetStreamSettings.Settings settings = CastingFaucetStreamSettings.get();
            float streamWidth = settings.width();
            float streamTop = settings.top();
            float streamBottom = settings.bottom();

            float u0 = sprite.getU(0.0F);
            float u1 = sprite.getU(streamWidth);
            float v0 = sprite.getV(0.0F);
            float v1 = sprite.getV(1.0F);

            // Full brightness (240 in both block + sky light)
            int light = 0x00F000F0;

            float half = streamWidth / 2.0F;
            float cx = settings.centerX();
            float cz = settings.centerZ();

            // Four faces of a thin vertical column

            // North face (z-)
            vtx(buffer, pose, cx - half, streamTop, cz - half, u0, v0, 0, 0, -1, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamBottom, cz - half, u0, v1, 0, 0, -1, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamBottom, cz - half, u1, v1, 0, 0, -1, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamTop, cz - half, u1, v0, 0, 0, -1, r, g, b, a, light);

            // South face (z+)
            vtx(buffer, pose, cx + half, streamTop, cz + half, u0, v0, 0, 0, 1, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamBottom, cz + half, u0, v1, 0, 0, 1, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamBottom, cz + half, u1, v1, 0, 0, 1, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamTop, cz + half, u1, v0, 0, 0, 1, r, g, b, a, light);

            // West face (x-)
            vtx(buffer, pose, cx - half, streamTop, cz + half, u0, v0, -1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamBottom, cz + half, u0, v1, -1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamBottom, cz - half, u1, v1, -1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx - half, streamTop, cz - half, u1, v0, -1, 0, 0, r, g, b, a, light);

            // East face (x+)
            vtx(buffer, pose, cx + half, streamTop, cz - half, u0, v0, 1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamBottom, cz - half, u0, v1, 1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamBottom, cz + half, u1, v1, 1, 0, 0, r, g, b, a, light);
            vtx(buffer, pose, cx + half, streamTop, cz + half, u1, v0, 1, 0, 0, r, g, b, a, light);
        });
    }

    private static void vtx(VertexConsumer buffer, PoseStack.Pose pose,
                            float x, float y, float z,
                            float u, float v,
                            float nx, float ny, float nz,
                            int r, int g, int b, int a,
                            int light) {
        buffer.addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    public static final class State extends BlockEntityRenderState {
        public boolean isPouring = false;
        public int fluidTint = 0;
    }
}
