package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fluids.FluidStack;

import java.awt.Color;

public class RenderHelper {

	public static void renderFluid(PoseStack poseStack, MultiBufferSource bufferSource, FluidStack fluid) {
		if(fluid != null && !fluid.isEmpty()) {
			poseStack.pushPose();
			float scale = 0.25f;
			poseStack.scale(scale, scale, scale);

			poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			poseStack.mulPose(Vector3f.YP.rotationDegrees(90F));
			poseStack.mulPose(Vector3f.ZP.rotationDegrees(270F));

			RenderType type = TransprotwoRenderTypes.getLiquid();
			VertexConsumer vertexConsumer = bufferSource.getBuffer(type);
			Matrix4f pose = poseStack.last().pose();

			Color color = new Color(fluid.getFluid().getAttributes().getColor(fluid));

			drawQuad(pose, vertexConsumer, color);

			if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
				bufferSource1.endBatch(type);
			}

			poseStack.popPose();
		}
	}

	public static void renderPower(PoseStack poseStack, MultiBufferSource bufferSource, Color color) {
		poseStack.pushPose();
		float scale = 0.1F;
		poseStack.scale(scale, scale, scale);

		poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		poseStack.mulPose(Vector3f.YP.rotationDegrees(90F));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(270F));

		RenderType type = TransprotwoRenderTypes.getPower();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(type);
		Matrix4f pose = poseStack.last().pose();

		drawQuad(pose, vertexConsumer, color);

		if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch(type);
		}

		poseStack.popPose();
	}

	private static void drawQuad(Matrix4f pose, VertexConsumer vertexConsumer, Color color) {
		float xOffset = -0.75f;
		float yOffset = -0f;
		float zOffset = -0.75f;

		vertexConsumer.vertex(pose, 0 + xOffset, yOffset, 1.5f + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1).endVertex();
		vertexConsumer.vertex(pose, 1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 1).endVertex();
		vertexConsumer.vertex(pose, 1 + xOffset + 0.5f, yOffset, 0 + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 0).endVertex();
		vertexConsumer.vertex(pose, 0 + xOffset, yOffset, 0 + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 0).endVertex();
	}
}
