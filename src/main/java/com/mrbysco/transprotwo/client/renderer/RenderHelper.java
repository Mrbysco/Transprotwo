package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class RenderHelper {

	public static void renderFluid(PoseStack poseStack, MultiBufferSource bufferSource, FluidStack fluid, int combinedLight) {
		if (fluid != null && !fluid.isEmpty()) {
			poseStack.pushPose();
			float scale = 0.25f;
			poseStack.scale(scale, scale, scale);

			poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			poseStack.mulPose(Axis.YP.rotationDegrees(90F));
			poseStack.mulPose(Axis.ZP.rotationDegrees(270F));

			RenderType type = TransprotwoRenderTypes.LIQUID;
			VertexConsumer vertexConsumer = bufferSource.getBuffer(type);
			Matrix4f pose = poseStack.last().pose();

			Color color = new Color(IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid));

			drawQuad(pose, vertexConsumer, color, combinedLight);

			poseStack.popPose();
		}
	}

	public static void renderPower(PoseStack poseStack, MultiBufferSource bufferSource, Color color) {
		poseStack.pushPose();
		float scale = 0.1F;
		poseStack.scale(scale, scale, scale);

		poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		poseStack.mulPose(Axis.YP.rotationDegrees(90F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(270F));

		RenderType type = TransprotwoRenderTypes.POWER;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(type);
		Matrix4f pose = poseStack.last().pose();

		drawQuad(pose, vertexConsumer, color, -1);

		poseStack.popPose();
	}

	private static void drawQuad(Matrix4f pose, VertexConsumer vertexConsumer, Color color, int packedLight) {
		float xOffset = -0.75f;
		float yOffset = -0f;
		float zOffset = -0.75f;

		// Define quad vertices
		float x1 = 0 + xOffset, y1 = yOffset, z1 = 1.5f + zOffset;
		float x2 = 1 + xOffset + 0.5f, y2 = yOffset, z2 = 1.5f + zOffset;
		float x3 = 1 + xOffset + 0.5f, y3 = yOffset, z3 = 0 + zOffset;
		float x4 = 0 + xOffset, y4 = yOffset, z4 = 0 + zOffset;

		// Calculate edges
		float edge1X = x2 - x1, edge1Y = y2 - y1, edge1Z = z2 - z1;
		float edge2X = x4 - x1, edge2Y = y4 - y1, edge2Z = z4 - z1;

		// Compute cross product for normal
		float normalX = edge1Y * edge2Z - edge1Z * edge2Y;
		float normalY = edge1Z * edge2X - edge1X * edge2Z;
		float normalZ = edge1X * edge2Y - edge1Y * edge2X;

		// Normalize the normal
		float length = (float) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
		normalX /= length;
		normalY /= length;
		normalZ /= length;

		// Add vertices with normals
		vertexConsumer.addVertex(pose, x1, y1, z1)
				.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.setNormal(normalX, normalY, normalZ)
				.setUv(0, 1)
				.setLight(packedLight);
		vertexConsumer.addVertex(pose, x2, y2, z2)
				.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.setNormal(normalX, normalY, normalZ)
				.setUv(1, 1)
				.setLight(packedLight);
		vertexConsumer.addVertex(pose, x3, y3, z3)
				.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.setNormal(normalX, normalY, normalZ)
				.setUv(1, 0)
				.setLight(packedLight);
		vertexConsumer.addVertex(pose, x4, y4, z4)
				.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
				.setNormal(normalX, normalY, normalZ)
				.setUv(0, 0)
				.setLight(packedLight);
	}
}
