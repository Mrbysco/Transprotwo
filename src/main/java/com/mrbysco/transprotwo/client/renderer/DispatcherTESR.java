package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.tile.AbstractDispatcherTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;

public class DispatcherTESR extends TileEntityRenderer<AbstractDispatcherTile> {

	public DispatcherTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(AbstractDispatcherTile dispatcher, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		final Minecraft mc = Minecraft.getInstance();
		final ClientPlayerEntity player = mc.player;

		if (player.inventory.getCurrentItem().isEmpty() || player.inventory.getCurrentItem().getItem() != TransprotwoRegistry.LINKER.get())
			return;

		BlockPos pos = dispatcher.getPos();
		matrixStack.push();
		float width = 5.0f;
		RenderType lineType = TransprotwoRenderTypes.getType(width);
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(lineType);

		final Color color = dispatcher.getColor();
		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			BlockPos pSubt = pa.getLeft().subtract(pos);
			float x = pSubt.getX() + .5f, y = pSubt.getY() + .5f, z = pSubt.getZ() + .5f;
			float x2 = 0 + .5f, y2 = 0 + .5f, z2 = 0 + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getWorld().getGameTime() / 10 % 2 != 0)
				continue;

			if (player.isSneaking()) {
				RenderSystem.disableDepthTest();
			} else {
				RenderSystem.enableDepthTest();
			}
			Matrix4f matrix = matrixStack.getLast().getMatrix();
			vertexBuilder.pos(matrix, x, y, z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f).endVertex();
			vertexBuilder.pos(matrix, x2, y2, z2).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f).endVertex();

		}

		if (vertexBuilder instanceof IRenderTypeBuffer.Impl) {
			((IRenderTypeBuffer.Impl) vertexBuilder).finish();
		}
		matrixStack.pop();
	}
}
