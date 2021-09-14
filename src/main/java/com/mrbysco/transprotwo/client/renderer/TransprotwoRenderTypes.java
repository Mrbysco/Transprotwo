package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class TransprotwoRenderTypes extends RenderType {
	public TransprotwoRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getType(float lineWidth) {
		return makeType("transprotwo:line",
				DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
				RenderType.State.getBuilder()
						.layer(VIEW_OFFSET_Z_LAYERING)
						.line(new LineState(OptionalDouble.of(lineWidth)))
						.transparency(NO_TRANSPARENCY)
						.target(ITEM_ENTITY_TARGET)
						.writeMask(COLOR_DEPTH_WRITE)
						.build(false));
	}

	public static RenderType getPower() {
		return makeType("transprotwo:power",
				DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 262144, false, true,
				RenderType.State.getBuilder()
						.texture(new RenderState.TextureState(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/power.png"), false, false))
						.transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
							RenderSystem.depthMask(true);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
						}))
						.build(true));
	}

	public static RenderType getLiquid() {
		return makeType("transprotwo:liquid",
				DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 262144, false, true,
				RenderType.State.getBuilder()
						.texture(new RenderState.TextureState(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/fluid.png"), false, false))
						.transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
							RenderSystem.depthMask(true);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
						}))
						.build(true));
	}
}