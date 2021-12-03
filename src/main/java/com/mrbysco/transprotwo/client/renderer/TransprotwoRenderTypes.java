package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalDouble;

public class TransprotwoRenderTypes extends RenderType {
	public TransprotwoRenderTypes(String nameIn, VertexFormat formatIn, Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType getType(float lineWidth) {
		return create("transprotwo:line",
				DefaultVertexFormat.POSITION_COLOR, Mode.LINES, 256, false, false,
				RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_LINES_SHADER)
						.setLineState(new LineStateShard(OptionalDouble.of(lineWidth)))
						.setLayeringState(VIEW_OFFSET_Z_LAYERING)
						.setTransparencyState(NO_TRANSPARENCY)
						.setOutputState(ITEM_ENTITY_TARGET)
						.setWriteMaskState(COLOR_DEPTH_WRITE)
						.setCullState(NO_CULL)
						.setDepthTestState(NO_DEPTH_TEST)
						.createCompositeState(false));
	}

	public static RenderType getPower() {
		return create("transprotwo:power",
				DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 262144, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/power.png"), false, false))
						.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
							RenderSystem.depthMask(false);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
							RenderSystem.defaultBlendFunc();
							RenderSystem.depthMask(true);
						}))
//						.setAlphaState(DEFAULT_ALPHA)
						.setCullState(NO_CULL)
						.createCompositeState(true));
	}

	public static RenderType getLiquid() {
		return create("transprotwo:liquid",
				DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 262144, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Transprotwo.MOD_ID, "textures/particle/fluid.png"), false, false))
						.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
							RenderSystem.depthMask(true);
							RenderSystem.enableBlend();
						}, () -> {
							RenderSystem.disableBlend();
							RenderSystem.defaultBlendFunc();
						}))
						.setCullState(NO_CULL)
						.createCompositeState(true));
	}
}