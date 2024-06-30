package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class TransprotwoRenderTypes extends RenderType {
	public TransprotwoRenderTypes(String nameIn, VertexFormat formatIn, Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static final RenderType LINE = create("transprotwo:line",
			DefaultVertexFormat.POSITION_COLOR, Mode.LINES, 256, false, false,
			RenderType.CompositeState.builder()
					.setShaderState(RENDERTYPE_LINES_SHADER)
					.setLineState(new LineStateShard(OptionalDouble.of(6.0f)))
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.setTransparencyState(NO_TRANSPARENCY)
					.setOutputState(ITEM_ENTITY_TARGET)
					.setWriteMaskState(COLOR_DEPTH_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.createCompositeState(false));

	public static final RenderType LINE_4 = create("transprotwo:line",
			DefaultVertexFormat.POSITION_COLOR, Mode.LINES, 256, false, false,
			RenderType.CompositeState.builder()
					.setShaderState(RENDERTYPE_LINES_SHADER)
					.setLineState(new LineStateShard(OptionalDouble.of(4.0f)))
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.setTransparencyState(NO_TRANSPARENCY)
					.setOutputState(ITEM_ENTITY_TARGET)
					.setWriteMaskState(COLOR_DEPTH_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.createCompositeState(false));

	public static final RenderType POWER = create("transprotwo:power",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 262144, false, true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(Transprotwo.modLoc("textures/particle/power.png"), false, false))
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

	public static final RenderType LIQUID = create("transprotwo:liquid",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 262144, false, true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(
							Transprotwo.modLoc("textures/particle/fluid.png"), false, false))
					.setLightmapState(RenderStateShard.LIGHTMAP)
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