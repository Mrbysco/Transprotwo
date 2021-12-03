package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.transprotwo.client.renderer.RenderHelper;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.tile.FluidDispatcherBE;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.FluidTransfer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.phys.Vec3;

public class FluidDispatcherBER extends AbstractDispatcherBER<FluidDispatcherBE> {
	public FluidDispatcherBER(Context context) {
		super(context);
	}

	@Override
	public void render(FluidDispatcherBE dispatcher, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		super.render(dispatcher, partialTicks, poseStack, bufferSource, combinedLightIn, combinedOverlayIn);

		if (!TransprotConfig.CLIENT.showFluids.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		final Vec3 projectedView = mc.gameRenderer.getMainCamera().getPosition();
		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if(abstractTransfer instanceof FluidTransfer transfer) {

				poseStack.pushPose();
				poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
				Vec3 cur = transfer.prev == null ? transfer.current : new Vec3(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTicks,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTicks,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTicks);
				poseStack.translate(cur.x, cur.y, cur.z);

				RenderSystem.disableDepthTest();
				RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack);
				int stackAmount = transfer.fluidStack.getAmount() / 1000;
				if (stackAmount > 1) {
					poseStack.translate(.08, .08, .08);
					RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack);
					if (stackAmount >= 16) {
						poseStack.translate(.08, .08, .08);
						RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack);
					}
				}
				RenderSystem.enableDepthTest();

				poseStack.popPose();
			}
		}
	}
}
