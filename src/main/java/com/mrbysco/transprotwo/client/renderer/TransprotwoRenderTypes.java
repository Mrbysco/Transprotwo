package com.mrbysco.transprotwo.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public abstract class TransprotwoRenderTypes extends RenderType {

	public TransprotwoRenderTypes(String p_173178_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}


	public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Transprotwo.modLoc("pipeline/lines_no_depth"))
			.withCull(false)
			.withDepthWrite(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build();

	public static final RenderPipeline.Snippet POWER_SNIPPET = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withVertexShader("core/position_tex_color")
			.withFragmentShader("core/position_tex_color")
			.withSampler("Sampler0")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(false)
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.buildSnippet();

	public static final RenderPipeline POWER_PIPELINE = RenderPipeline.builder(POWER_SNIPPET)
			.withLocation(Transprotwo.modLoc("pipeline/power"))
			.withCull(false)
			.withDepthWrite(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build();

	public static final RenderType LINE = RenderType.create("transprotwo:line", 256, LINES_NO_DEPTH, RenderType.CompositeState.builder()
			.setLineState(new LineStateShard(OptionalDouble.of(6.0)))
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.setOutputState(ITEM_ENTITY_TARGET)
			.createCompositeState(false));

	public static final RenderType LINE_4 = RenderType.create("transprotwo:line", 256, LINES_NO_DEPTH, RenderType.CompositeState.builder()
			.setLineState(new LineStateShard(OptionalDouble.of(4.0)))
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.setOutputState(ITEM_ENTITY_TARGET)
			.createCompositeState(false));

	public static final RenderType POWER = RenderType.create("transprotwo:power", 262144, POWER_PIPELINE,
			RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(Transprotwo.modLoc("textures/particle/power.png"), false))
//					.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
//						RenderSystem.depthMask(false);
//						RenderSystem.enableBlend();
//					}, () -> {
//						RenderSystem.disableBlend();
//						RenderSystem.defaultBlendFunc();
//						RenderSystem.depthMask(true);
//					}))
					.createCompositeState(true));

	public static final RenderType LIQUID = RenderType.create("transprotwo:liquid", 262144, POWER_PIPELINE,
			RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(
							Transprotwo.modLoc("textures/particle/fluid.png"), false))
					.setLightmapState(RenderStateShard.LIGHTMAP)
//					.setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
//						RenderSystem.depthMask(true);
//						RenderSystem.enableBlend();
//					}, () -> {
//						RenderSystem.disableBlend();
//						RenderSystem.defaultBlendFunc();
//					}))
					.createCompositeState(true));
}