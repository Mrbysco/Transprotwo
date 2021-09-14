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
			if(mc.world != null && mc.player != null) {
				for (int i = 0; i < mc.world.loadedTileEntityList.size(); i++) {
					TileEntity t = mc.world.loadedTileEntityList.get(i);
					if (t instanceof ItemDispatcherTile && getDistance(mc.player, t.getPos().getX(), t.getPos().getY(), t.getPos().getZ()) < 32)
						renderItemTransfers(matrixStack, (ItemDispatcherTile) t, t.getPos().getX(), t.getPos().getY(), t.getPos().getZ(), event.getPartialTicks());
					if (t instanceof FluidDispatcherTile && getDistance(mc.player, t.getPos().getX(), t.getPos().getY(), t.getPos().getZ()) < 32)
						renderFluidTransfers(matrixStack, (FluidDispatcherTile) t, t.getPos().getX(), t.getPos().getY(), t.getPos().getZ(), event.getPartialTicks());
					if (t instanceof PowerDispatcherTile && getDistance(mc.player, t.getPos().getX(), t.getPos().getY(), t.getPos().getZ()) < 32)
						renderEnergyBeam(matrixStack, (PowerDispatcherTile) t, event.getPartialTicks());
				}
			}
		} catch (IndexOutOfBoundsException e) {
		}
	}

	private double getDistance(PlayerEntity player, double x, double y, double z) {
		float f = (float)(player.getPosX() - x);
		float f1 = (float)(player.getPosY() - y);
		float f2 = (float)(player.getPosZ() - z);
		return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	public void renderItemTransfers(MatrixStack matrixStack, ItemDispatcherTile te, double x, double y, double z, float partialTicks) {
		if (!TransprotConfig.CLIENT.showItems.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
		final IRenderTypeBuffer.Impl typeBuffer = mc.getRenderTypeBuffers().getBufferSource();
		for (AbstractTransfer abstractTransfer : te.getTransfers()) {
			if(abstractTransfer instanceof ItemTransfer) {
				ItemTransfer transfer = (ItemTransfer) abstractTransfer;

				matrixStack.push();
				matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
				matrixStack.translate(x, y, z);
				Vector3d cur = transfer.prev == null ? transfer.current : new Vector3d(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				matrixStack.translate(cur.x, cur.y, cur.z);

				int combinedLightIn = WorldRenderer.getCombinedLight(mc.world, new BlockPos(cur.add(x, y, z)));
				if (mc.gameSettings.graphicFanciness.func_238162_a_() > 0 && !mc.isGamePaused()) {
					float rotation = (float) (720.0 * ((System.currentTimeMillis() + transfer.turn) & 0x3FFFL) / 0x3FFFL);
					matrixStack.rotate(Vector3f.YP.rotationDegrees(rotation));
				}
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				RenderSystem.disableDepthTest();
				ItemRenderer itemRenderer = mc.getItemRenderer();
				itemRenderer.renderItem(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
				if (transfer.stack.getCount() > 1) {
					matrixStack.translate(.08, .08, .08);
					itemRenderer.renderItem(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
					if (transfer.stack.getCount() >= 16) {
						matrixStack.translate(.08, .08, .08);
						itemRenderer.renderItem(transfer.stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStack, typeBuffer);
					}
				}
				RenderSystem.enableDepthTest();

				typeBuffer.finish();
				matrixStack.pop();
			}
		}
	}

	public void renderFluidTransfers(MatrixStack matrixStack, FluidDispatcherTile te, double x, double y, double z, float partialTicks) {
		if (!TransprotConfig.CLIENT.showFluids.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
		final IRenderTypeBuffer.Impl typeBuffer = mc.getRenderTypeBuffers().getBufferSource();
		for (AbstractTransfer abstractTransfer : te.getTransfers()) {
			if(abstractTransfer instanceof FluidTransfer) {
				FluidTransfer transfer = (FluidTransfer) abstractTransfer;

				matrixStack.push();
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

				matrixStack.pop();
			}
		}
	}

	public void renderEnergyBeam(MatrixStack matrixStack, PowerDispatcherTile dispatcher, float partialTicks) {
		if (!TransprotConfig.CLIENT.showPower.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vector3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
		final IRenderTypeBuffer.Impl typeBuffer = mc.getRenderTypeBuffers().getBufferSource();

		BlockPos pos = dispatcher.getPos();
		matrixStack.push();
		matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		float width = 4.0f;
		RenderType lineType = TransprotwoRenderTypes.getType(width);
		IVertexBuilder vertexBuilder = typeBuffer.getBuffer(lineType);
		Color[] colors = dispatcher.getColors();

		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if(abstractTransfer instanceof PowerTransfer) {
				PowerTransfer transfer = (PowerTransfer) abstractTransfer;

				matrixStack.push();
				matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
				matrixStack.translate(dispatcher.getPos().getX(), dispatcher.getPos().getY(), dispatcher.getPos().getZ());
				Vector3d cur = transfer.prev == null ? transfer.current : new Vector3d(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				matrixStack.translate(cur.x, cur.y, cur.z);

				RenderSystem.disableDepthTest();
				RenderHelper.renderPower(matrixStack, typeBuffer, colors[2]);
				int stackAmount = transfer.powerStack.getAmount();
				if (stackAmount > 1) {
					matrixStack.translate(.08, .08, .08);
					RenderHelper.renderPower(matrixStack, typeBuffer, colors[2]);
					if (stackAmount >= 16) {
						matrixStack.translate(.08, .08, .08);
						RenderHelper.renderPower(matrixStack, typeBuffer, colors[2]);
					}
				}
				RenderSystem.enableDepthTest();

				matrixStack.pop();
			}
		}
		for (Pair<BlockPos, Direction> pa : dispatcher.getTargets()) {
			BlockPos p = pa.getLeft();
			float x = p.getX() + .5f, y = p.getY() + .5f, z = p.getZ() + .5f;
			float x2 = pos.getX() + .5f, y2 = pos.getY() + .5f, z2 = pos.getZ() + .5f;
			boolean free = dispatcher.wayFree(pos, p);
			if (!free && dispatcher.getWorld().getGameTime() / 10 % 2 != 0)
				continue;

			if (mc.player.isSneaking()) {
				RenderSystem.disableDepthTest();
			} else {
				RenderSystem.enableDepthTest();
			}
			Matrix4f matrix = matrixStack.getLast().getMatrix();

			float offset = 0.015F;
			float initialOffset = offset * 2;
			Direction dir = Direction.getFacingFromVector(x - x2, y - y2, z - z2);
			if(y != y2 && (dir == Direction.UP || dir == Direction.DOWN)) {
				for(int i = 0; i < 5; i++) {
					vertexBuilder.pos(matrix, x - initialOffset + (i * offset), y, z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.pos(matrix, x2 - initialOffset + (i * offset), y2, z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				}
			} else {
				for(int i = 0; i < 5; i++) {
					vertexBuilder.pos(matrix, x, y - initialOffset + (i * offset), z).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
					vertexBuilder.pos(matrix, x2, y2 - initialOffset + (i * offset), z2).color(colors[i].getRed() / 255f, colors[i].getGreen() / 255f, colors[i].getBlue() / 255f, 1f).endVertex();
				}
			}
		}

		typeBuffer.finish(lineType);

		matrixStack.pop();
	}
}
