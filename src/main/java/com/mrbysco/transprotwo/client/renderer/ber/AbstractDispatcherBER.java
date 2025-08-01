package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class AbstractDispatcherBER<T extends AbstractDispatcherBE> implements BlockEntityRenderer<T> {

	public AbstractDispatcherBER(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(T dispatcher, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 p_401186_) {
		final Minecraft mc = Minecraft.getInstance();
		final LocalPlayer player = mc.player;
		if (player == null)
			return;
		if (player.getInventory().getSelectedItem().isEmpty() || player.getInventory().getSelectedItem().getItem() != TransprotwoRegistry.LINKER.get())
			return;

		BlockPos pos = dispatcher.getBlockPos();
		poseStack.pushPose();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(TransprotwoRenderTypes.LINE);

		final Color color = dispatcher.getColor();
		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			BlockPos subtracted = pa.getLeft().subtract(pos);
			float x = subtracted.getX() + .5f, y = subtracted.getY() + .5f, z = subtracted.getZ() + .5f;
			float x2 = 0 + .5f, y2 = 0 + .5f, z2 = 0 + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getLevel() != null && dispatcher.getLevel().getGameTime() / 10 % 2 != 0)
				continue;

			float dx = x2 - x;
			float dy = y2 - y;
			float dz = z2 - z;
			float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
			float nx = dx / length;
			float ny = dy / length;
			float nz = dz / length;

			Matrix4f matrix = poseStack.last().pose();
			vertexConsumer.addVertex(matrix, x, y, z).setNormal(nx, ny, nz).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			vertexConsumer.addVertex(matrix, x2, y2, z2).setNormal(nx, ny, nz).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}

		if (vertexConsumer instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch();
		}
		poseStack.popPose();
	}

	@Override
	public boolean shouldRender(T dispatcher, Vec3 pos) {
		return Vec3.atCenterOf(dispatcher.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pos.multiply(1.0D, 0.0D, 1.0D), this.getViewDistance());
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull T dispatcher) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	@NotNull
	public AABB getRenderBoundingBox(T blockEntity) {
		return new AABB(blockEntity.getBlockPos()).inflate(16.0D);
	}
}
