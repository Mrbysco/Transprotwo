package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleRenderTypes {
	public static final ParticleRenderType SQUARE_RENDER = new ParticleRenderType() {

		@Override
		public void begin(BufferBuilder buffer, TextureManager textureManager) {
//			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
//			RenderSystem.alphaFunc(516, 0.3f);
			RenderSystem.enableCull();
//			textureManager.bind(TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.depthMask(false);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA.value, DestFactor.ONE_MINUS_SRC_ALPHA.value);
			buffer.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		@Override
		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.enableDepthTest();
//			RenderSystem.enableAlphaTest();
			RenderSystem.depthMask(true);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA.value, DestFactor.ONE_MINUS_SRC_ALPHA.value);
			RenderSystem.disableCull();
//			RenderSystem.alphaFunc(516, 0.1F);
		}

		@Override
		public String toString() {
			return "transprotwo:square";
		}
	};
}
