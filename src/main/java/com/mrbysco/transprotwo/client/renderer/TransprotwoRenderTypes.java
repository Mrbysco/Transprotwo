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
		return create("transprotwo:line",
				DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
				RenderType.State.builder()
						.setLayeringState(VIEW_OFFSET_Z_LAYERING)
						.setLineState(new LineState(OptionalDouble.of(lineWidth)))
						.setTransparencyState(NO_TRANSPARENCY)
						.setOutputState(ITEM_ENTITY_TARGET)
						.setWriteMaskState(COLOR_DEPTH_WRITE)
						.createCompositeState(false));
	}

	public static RenderType getPower() {
		return create("transprotwo:power",
				DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 262144, false, true,
				RenderType.State.builder()
						.setTextureState(new RenderState.TextureState(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/power.png"), false, false))
						.setTransparencyState(new RenderState.TransparencyState("translucent_transparency", () -> {
							RenderSystem.depthMask(false);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
							RenderSystem.defaultBlendFunc();
							RenderSystem.depthMask(true);
						}))
						.setAlphaState(DEFAULT_ALPHA)
						.createCompositeState(true));
	}

	public static RenderType getLiquid() {
		return create("transprotwo:liquid",
				DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 262144, false, true,
				RenderType.State.builder()
						.setTextureState(new RenderState.TextureState(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/fluid.png"), false, false))
						.setTransparencyState(new RenderState.TransparencyState("translucent_transparency", () -> {
							RenderSystem.depthMask(true);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
							RenderSystem.defaultBlendFunc();
						}))
						.createCompositeState(true));
	}
}