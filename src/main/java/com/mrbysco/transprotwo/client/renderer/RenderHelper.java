package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;

import java.awt.Color;

public class RenderHelper {

	public static void renderFluid(MatrixStack matrixStack, IRenderTypeBuffer.Impl typeBuffer, FluidStack fluid) {
		if(fluid != null && !fluid.isEmpty()) {
			matrixStack.pushPose();
			float scale = 0.25f;
			matrixStack.scale(scale, scale, scale);

			matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(90F));
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(270F));

			RenderType type = TransprotwoRenderTypes.getLiquid();
			IVertexBuilder buffer = typeBuffer.getBuffer(type);
			Matrix4f matrix = matrixStack.last().pose();

			Color color = new Color(fluid.getFluid().getAttributes().getColor(fluid));

			drawQuad(matrix, buffer, color);

			typeBuffer.endBatch(type);

			matrixStack.popPose();
		}
	}

	public static void renderPower(MatrixStack matrixStack, IRenderTypeBuffer.Impl typeBuffer, Color color) {
		matrixStack.pushPose();
		float scale = 0.1F;
		matrixStack.scale(scale, scale, scale);

		matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(90F));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(270F));

		RenderType type = TransprotwoRenderTypes.getPower();
		IVertexBuilder buffer = typeBuffer.getBuffer(type);
		Matrix4f matrix = matrixStack.last().pose();

		drawQuad(matrix, buffer, color);

		typeBuffer.endBatch(type);

		matrixStack.popPose();
	}

	private static void drawQuad(Matrix4f matrix, IVertexBuilder buffer, Color color) {
		float xOffset = -0.75f;
		float yOffset = -0f;
		float zOffset = -0.75f;

		buffer.vertex(matrix, 0 + xOffset, yOffset, 1.5f + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1).endVertex();
		buffer.vertex(matrix, 1 + xOffset + 0.5f, yOffset, 1.5f + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 1).endVertex();
		buffer.vertex(matrix, 1 + xOffset + 0.5f, yOffset, 0 + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 0).endVertex();
		buffer.vertex(matrix, 0 + xOffset, yOffset, 0 + zOffset).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 0).endVertex();
	}
}
