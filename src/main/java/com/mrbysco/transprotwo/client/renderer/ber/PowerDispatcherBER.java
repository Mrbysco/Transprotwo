package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mrbysco.transprotwo.client.renderer.RenderHelper;
import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.tile.FluidDispatcherBE;
import com.mrbysco.transprotwo.tile.PowerDispatcherBE;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.FluidTransfer;
import com.mrbysco.transprotwo.tile.transfer.power.PowerTransfer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;

public class PowerDispatcherBER extends AbstractDispatcherBER<PowerDispatcherBE> {
	public PowerDispatcherBER(Context context) {
		super(context);
	}

	@Override
	public void render(PowerDispatcherBE dispatcher, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		super.render(dispatcher, partialTicks, poseStack, bufferSource, combinedLightIn, combinedOverlayIn);

		if (!TransprotConfig.CLIENT.showPower.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vec3 projectedView = mc.gameRenderer.getMainCamera().getPosition();

		BlockPos pos = dispatcher.getBlockPos();
		poseStack.pushPose();
		poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Color[] colors = dispatcher.getColors();

		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if(abstractTransfer instanceof PowerTransfer transfer) {

				poseStack.pushPose();
				poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
				Vec3 cur = transfer.prev == null ? transfer.current : new Vec3(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				poseStack.translate(cur.x, cur.y, cur.z);

				RenderSystem.disableDepthTest();
				RenderHelper.renderPower(poseStack, bufferSource, colors[2]);
				RenderSystem.enableDepthTest();

				poseStack.popPose();
			}
		}
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		float width = 4.0f;
		RenderType lineType = TransprotwoRenderTypes.getType(width);
		VertexConsumer vertexBuilder = bufferSource.getBuffer(lineType);

		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			float x = p.getX() + .5f, y = p.getY() + .5f, z = p.getZ() + .5f;
			float x2 = pos.getX() + .5f, y2 = pos.getY() + .5f, z2 = pos.getZ() + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getLevel().getGameTime() / 10 % 2 != 0)
				continue;

			Matrix4f pose = poseStack.last().pose();

			float offset = 0.015F;
			float initialOffset = offset * 2;
			Direction dir = Direction.getNearest(x - x2, y - y2, z - z2);
			boolean flag = y != y2 && (dir == Direction.UP || dir == Direction.DOWN);

			for(int i = 0; i < 5; i++) {
				if(flag) {
					vertexBuilder.vertex(pose, x - initialOffset + (i * offset), y, z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.vertex(pose, x2 - initialOffset + (i * offset), y2, z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				} else {
					vertexBuilder.vertex(pose, x, y - initialOffset + (i * offset), z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.vertex(pose, x2, y2 - initialOffset + (i * offset), z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				}
			}
		}

		if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch(lineType);
		}
		poseStack.popPose();
	}
}
