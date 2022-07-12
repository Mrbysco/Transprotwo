package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.ItemTransfer;
import com.mrbysco.transprotwo.config.TransprotConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class ItemDispatcherBER extends AbstractDispatcherBER<ItemDispatcherBE> {
	public ItemDispatcherBER(Context context) {
		super(context);
	}

	@Override
	public void render(ItemDispatcherBE dispatcher, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		super.render(dispatcher, partialTicks, poseStack, bufferSource, combinedLightIn, combinedOverlayIn);

		if (!TransprotConfig.CLIENT.showItems.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final BlockPos pos = dispatcher.getBlockPos();
		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if (abstractTransfer instanceof ItemTransfer transfer) {
				poseStack.pushPose();
				Vec3 cur = transfer.prev == null ? transfer.current : new Vec3(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				poseStack.translate(cur.x, cur.y, cur.z);

				int newCombinedIn = LevelRenderer.getLightColor(mc.level, new BlockPos(cur.add(pos.getX(), pos.getY(), pos.getZ())));
				if (mc.options.graphicsMode().get().getId() > 0 && !mc.isPaused()) {
					float rotation = (float) (720.0 * ((System.currentTimeMillis() + transfer.turn) & 0x3FFFL) / 0x3FFFL);
					poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
				}
				poseStack.scale(0.5F, 0.5F, 0.5F);
				RenderSystem.disableDepthTest();
				ItemRenderer itemRenderer = mc.getItemRenderer();
				itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, newCombinedIn, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
				if (transfer.stack.getCount() > 1) {
					poseStack.translate(.08, .08, .08);
					itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, newCombinedIn, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
					if (transfer.stack.getCount() >= 16) {
						poseStack.translate(.08, .08, .08);
						itemRenderer.renderStatic(transfer.stack, TransformType.FIXED, newCombinedIn, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
					}
				}
				RenderSystem.enableDepthTest();

				if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
					bufferSource1.endBatch();
				}
				poseStack.popPose();
			}
		}
	}
}
