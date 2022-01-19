package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;

public class AbstractDispatcherBER<T extends AbstractDispatcherBE> implements BlockEntityRenderer<T> {

	public AbstractDispatcherBER(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(T dispatcher, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		final Minecraft mc = Minecraft.getInstance();
		final LocalPlayer player = mc.player;

		if (player.getInventory().getSelected().isEmpty() || player.getInventory().getSelected().getItem() != TransprotwoRegistry.LINKER.get())
			return;

		BlockPos pos = dispatcher.getBlockPos();
		poseStack.pushPose();
		float width = 6.0f;
		RenderType lineType = TransprotwoRenderTypes.getType(width);
		VertexConsumer vertexConsumer = bufferSource.getBuffer(lineType);

		final Color color = dispatcher.getColor();
		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			BlockPos pSubt = pa.getLeft().subtract(pos);
			float x = pSubt.getX() + .5f, y = pSubt.getY() + .5f, z = pSubt.getZ() + .5f;
			float x2 = 0 + .5f, y2 = 0 + .5f, z2 = 0 + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getLevel().getGameTime() / 10 % 2 != 0)
				continue;

			if (player.isShiftKeyDown()) {
				RenderSystem.disableDepthTest();
			} else {
				RenderSystem.enableDepthTest();
			}
			Matrix4f matrix = poseStack.last().pose();
			vertexConsumer.vertex(matrix, x, y, z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f).endVertex();
			vertexConsumer.vertex(matrix, x2, y2, z2).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f).endVertex();
		}

		if (vertexConsumer instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch();
		}
		poseStack.popPose();
	}

	@Override
	public boolean shouldRender(T dispatcher, Vec3 pos) {
		return Vec3.atCenterOf(dispatcher.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pos.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
	}

	@Override
	public boolean shouldRenderOffScreen(T dispatcher) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
