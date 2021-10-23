package com.mrbysco.transprotwo.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrbysco.transprotwo.client.renderer.RenderHelper;
import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
import com.mrbysco.transprotwo.tile.ItemDispatcherTile;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.FluidTransfer;
import com.mrbysco.transprotwo.tile.transfer.ItemTransfer;
import com.mrbysco.transprotwo.tile.transfer.power.PowerTransfer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;

public class TransferRenderer {

	@SubscribeEvent
	public void ren(RenderWorldLastEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		MatrixStack matrixStack = event.getMatrixStack();
		try {
			if(mc.level != null && mc.player != null) {
				for (int i = 0; i < mc.level.blockEntityList.size(); i++) {
					TileEntity t = mc.level.blockEntityList.get(i);
					if (t instanceof ItemDispatcherTile && getDistance(mc.player, t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ()) < 32)
						renderItemTransfers(matrixStack, (ItemDispatcherTile) t, t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ(), event.getPartialTicks());
					if (t instanceof FluidDispatcherTile && getDistance(mc.player, t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ()) < 32)
						renderFluidTransfers(matrixStack, (FluidDispatcherTile) t, t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ(), event.getPartialTicks());
					if (t instanceof PowerDispatcherTile && getDistance(mc.player, t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ()) < 32)
						renderEnergyBeam(matrixStack, (PowerDispatcherTile) t, event.getPartialTicks());
				}
			}
		} catch (IndexOutOfBoundsException e) {
		}
	}

	private double getDistance(PlayerEntity player, double x, double y, double z) {
		float f = (float)(player.getX() - x);
		float f1 = (float)(player.getY() - y);
		float f2 = (float)(player.getZ() - z);
		return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	public void renderItemTransfers(MatrixStack matrixStack, ItemDispatcherTile te, double x, double y, double z, float partialTicks) {
		if (!TransprotConfig.CLIENT.showItems.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getMainCamera().getPosition();
		final IRenderTypeBuffer.Impl typeBuffer = mc.renderBuffers().bufferSource();
		for (AbstractTransfer abstractTransfer : te.getTransfers()) {
			if(abstractTransfer instanceof ItemTransfer) {
				ItemTransfer transfer = (ItemTransfer) abstractTransfer;

				matrixStack.pushPose();
				matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
				matrixStack.translate(x, y, z);
				Vector3d cur = transfer.prev == null ? transfer.current : new Vector3d(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				matrixStack.translate(cur.x, cur.y, cur.z);

				int combinedLightIn = WorldRenderer.getLightColor(mc.level, new BlockPos(cur.add(x, y, z)));
				if (mc.options.graphicsMode.getId() > 0 && !mc.isPaused()) {
					float rotation = (float) (720.0 * ((System.currentTimeMillis() + transfer.turn) & 0x3FFFL) / 0x3FFFL);
					matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
				}
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				RenderSystem.disableDepthTest();
				ItemRenderer itemRenderer = mc.getItemRenderer();
				itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
				if (transfer.stack.getCount() > 1) {
					matrixStack.translate(.08, .08, .08);
					itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
					if (transfer.stack.getCount() >= 16) {
						matrixStack.translate(.08, .08, .08);
						itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
					}
				}
				RenderSystem.enableDepthTest();

				typeBuffer.endBatch();
				matrixStack.popPose();
			}
		}
	}

	public void renderFluidTransfers(MatrixStack matrixStack, FluidDispatcherTile te, double x, double y, double z, float partialTicks) {
		if (!TransprotConfig.CLIENT.showFluids.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getMainCamera().getPosition();
		final IRenderTypeBuffer.Impl typeBuffer = mc.renderBuffers().bufferSource();
		for (AbstractTransfer abstractTransfer : te.getTransfers()) {
			if(abstractTransfer instanceof FluidTransfer) {
				FluidTransfer transfer = (FluidTransfer) abstractTransfer;

				matrixStack.pushPose();
				matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
				matrixStack.translate(x, y, z);
				Vector3d cur = transfer.prev == null ? transfer.current : new Vector3d(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				matrixStack.translate(cur.x, cur.y, cur.z);

				RenderSystem.disableDepthTest();
				RenderHelper.renderFluid(matrixStack, typeBuffer, transfer.fluidStack);
				int stackAmount = transfer.fluidStack.getAmount() / 1000;
				if (stackAmount > 1) {
					matrixStack.translate(.08, .08, .08);
					RenderHelper.renderFluid(matrixStack, typeBuffer, transfer.fluidStack);
					if (stackAmount >= 16) {
						matrixStack.translate(.08, .08, .08);
						RenderHelper.renderFluid(matrixStack, typeBuffer, transfer.fluidStack);
					}
				}
				RenderSystem.enableDepthTest();

				matrixStack.popPose();
			}
		}
	}

	public void renderEnergyBeam(MatrixStack matrixStack, PowerDispatcherTile dispatcher, float partialTicks) {
		if (!TransprotConfig.CLIENT.showPower.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getMainCamera().getPosition();
		final IRenderTypeBuffer.Impl typeBuffer = mc.renderBuffers().bufferSource();

		BlockPos pos = dispatcher.getBlockPos();
		matrixStack.pushPose();
		matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Color[] colors = dispatcher.getColors();

		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if(abstractTransfer instanceof PowerTransfer) {
				PowerTransfer transfer = (PowerTransfer) abstractTransfer;

				matrixStack.pushPose();
				matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
				Vector3d cur = transfer.prev == null ? transfer.current : new Vector3d(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				matrixStack.translate(cur.x, cur.y, cur.z);

				RenderSystem.disableDepthTest();
				RenderHelper.renderPower(matrixStack, typeBuffer, colors[2]);
				RenderSystem.enableDepthTest();

				matrixStack.popPose();
			}
		}
		matrixStack.popPose();

		matrixStack.pushPose();
		matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		float width = 4.0f;
		RenderType lineType = TransprotwoRenderTypes.getType(width);
		IVertexBuilder vertexBuilder = typeBuffer.getBuffer(lineType);

		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			float x = p.getX() + .5f, y = p.getY() + .5f, z = p.getZ() + .5f;
			float x2 = pos.getX() + .5f, y2 = pos.getY() + .5f, z2 = pos.getZ() + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getLevel().getGameTime() / 10 % 2 != 0)
				continue;

			Matrix4f matrix = matrixStack.last().pose();

			float offset = 0.015F;
			float initialOffset = offset * 2;
			Direction dir = Direction.getNearest(x - x2, y - y2, z - z2);
			boolean flag = y != y2 && (dir == Direction.UP || dir == Direction.DOWN);

			for(int i = 0; i < 5; i++) {
				if(flag) {
					vertexBuilder.vertex(matrix, x - initialOffset + (i * offset), y, z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.vertex(matrix, x2 - initialOffset + (i * offset), y2, z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				} else {
					vertexBuilder.vertex(matrix, x, y - initialOffset + (i * offset), z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.vertex(matrix, x2, y2 - initialOffset + (i * offset), z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				}
			}
		}

		typeBuffer.endBatch(lineType);
		matrixStack.popPose();
	}
}
