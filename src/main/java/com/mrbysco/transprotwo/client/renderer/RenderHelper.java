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
			matrixStack.push();
			float scale = 0.25f;
			matrixStack.scale(scale, scale, scale);

			matrixStack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
			matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(270F));

			RenderType type = TransprotwoRenderTypes.getLiquid();
			IVertexBuilder buffer = typeBuffer.getBuffer(type);
			Matrix4f matrix = matrixStack.getLast().getMatrix();

			Color color = new Color(fluid.getFluid().getAttributes().getColor(fluid));
			drawQuad(matrix, buffer, color);

			typeBuffer.finish(type);

			matrixStack.pop();
		}
	}

	public static void renderPower(MatrixStack matrixStack, IRenderTypeBuffer.Impl typeBuffer, Color color) {
		matrixStack.push();
		float scale = 0.25f;
		matrixStack.scale(scale, scale, scale);

		matrixStack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
		matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(270F));

		RenderType type = TransprotwoRenderTypes.getPower();
		IVertexBuilder buffer = typeBuffer.getBuffer(type);
		Matrix4f matrix = matrixStack.getLast().getMatrix();

		drawQuad(matrix, buffer, color);

		typeBuffer.finish(type);

		matrixStack.pop();
	}

	private static void drawQuad(Matrix4f matrix, IVertexBuilder buffer, Color color) {
		buffer.pos(matrix, 0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(0, 1).endVertex();
		buffer.pos(matrix, 0, 0, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(1, 1).endVertex();
		buffer.pos(matrix, 1, 0, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(1, 0).endVertex();
		buffer.pos(matrix, 1, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(0, 0).endVertex();
	}
}
