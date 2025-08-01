package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.client.renderer.RenderHelper;
import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class PowerDispatcherBER extends AbstractDispatcherBER<PowerDispatcherBE> {
	public PowerDispatcherBER(Context context) {
		super(context);
	}

	@Override
	public void render(PowerDispatcherBE dispatcher, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
		super.render(dispatcher, partialTick, poseStack, bufferSource, packedLight, packedOverlay, cameraPos);

		if (!TransprotConfig.CLIENT.showPower.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vec3 projectedView = mc.gameRenderer.getMainCamera().getPosition();

		BlockPos pos = dispatcher.getBlockPos();
		poseStack.pushPose();
		poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Color[] colors = dispatcher.getColors();

		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if (abstractTransfer instanceof PowerTransfer transfer) {

				poseStack.pushPose();
				poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
				Vec3 cur = transfer.prev == null ? transfer.current : new Vec3(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTick,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTick,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTick);
				poseStack.translate(cur.x, cur.y, cur.z);

				RenderHelper.renderPower(poseStack, bufferSource, colors[2]);

				poseStack.popPose();
			}
		}
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		RenderType lineType = TransprotwoRenderTypes.POWER;
		VertexConsumer vertexBuilder = bufferSource.getBuffer(lineType);

		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getFirst();
			float x = p.getX() + .5f, y = p.getY() + .5f, z = p.getZ() + .5f;
			float x2 = pos.getX() + .5f, y2 = pos.getY() + .5f, z2 = pos.getZ() + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getLevel().getGameTime() / 10 % 2 != 0)
				continue;

			Matrix4f pose = poseStack.last().pose();

			float offset = 0.015F;
			float initialOffset = offset * 2;
			Direction dir = Direction.getNearest((int) (x - x2), (int) (y - y2), (int) (z - z2), null); //TODO: Double check! as it was using float before
			boolean flag = y != y2 && (dir == Direction.UP || dir == Direction.DOWN);

			float dx = x2 - x;
			float dy = y2 - y;
			float dz = z2 - z;
			float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
			float nx = dx / length;
			float ny = dy / length;
			float nz = dz / length;

			for (int i = 0; i < 5; i++) {
				if (flag) {
					vertexBuilder.addVertex(pose, x - initialOffset + (i * offset), y, z)
							.setNormal(nx, ny, nz)
							.setColor(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f);
					vertexBuilder.addVertex(pose, x2 - initialOffset + (i * offset), y2, z2)
							.setNormal(nx, ny, nz)
							.setColor(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f);
				} else {
					vertexBuilder.addVertex(pose, x, y - initialOffset + (i * offset), z)
							.setNormal(nx, ny, nz)
							.setColor(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f);
					vertexBuilder.addVertex(pose, x2, y2 - initialOffset + (i * offset), z2)
							.setNormal(nx, ny, nz)
							.setColor(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f);
				}
			}
		}

		if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch(lineType);
		}
		poseStack.popPose();
	}
}
