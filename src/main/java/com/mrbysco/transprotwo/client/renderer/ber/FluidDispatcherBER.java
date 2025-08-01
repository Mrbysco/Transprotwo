package com.mrbysco.transprotwo.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.FluidTransfer;
import com.mrbysco.transprotwo.client.renderer.RenderHelper;
import com.mrbysco.transprotwo.config.TransprotConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.phys.Vec3;

public class FluidDispatcherBER extends AbstractDispatcherBER<FluidDispatcherBE> {
	public FluidDispatcherBER(Context context) {
		super(context);
	}

	@Override
	public void render(FluidDispatcherBE dispatcher, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 p_401186_) {
		super.render(dispatcher, partialTick, poseStack, bufferSource, packedLight, packedOverlay, p_401186_);

		if (!TransprotConfig.CLIENT.showFluids.get())
			return;

		final Minecraft mc = Minecraft.getInstance();
		for (AbstractTransfer abstractTransfer : dispatcher.getTransfers()) {
			if (abstractTransfer instanceof FluidTransfer transfer) {

				poseStack.pushPose();
				Vec3 cur = transfer.prev == null ? transfer.current : new Vec3(
						transfer.prev.x + (transfer.current.x - transfer.prev.x) * partialTick,
						transfer.prev.y + (transfer.current.y - transfer.prev.y) * partialTick,
						transfer.prev.z + (transfer.current.z - transfer.prev.z) * partialTick);
				poseStack.translate(cur.x, cur.y, cur.z);

//				RenderSystem.disableDepthTest();
				RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack, packedLight);
				int stackAmount = transfer.fluidStack.getAmount() / 1000;
				if (stackAmount > 1) {
					poseStack.translate(.08, .08, .08);
					RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack, packedLight);
					if (stackAmount >= 16) {
						poseStack.translate(.08, .08, .08);
						RenderHelper.renderFluid(poseStack, bufferSource, transfer.fluidStack, packedLight);
					}
				}
//				RenderSystem.enableDepthTest();

				poseStack.popPose();
			}
		}
	}
}
