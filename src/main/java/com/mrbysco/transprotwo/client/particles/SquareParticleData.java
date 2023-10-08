package com.mrbysco.transprotwo.client.particles;

import com.mrbysco.transprotwo.client.particles.factory.ParticleColor;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleTypeData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class SquareParticleData implements ParticleProvider<SquareParticleTypeData> {
	private final SpriteSet spriteSet;

	public SquareParticleData(SpriteSet sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle createParticle(SquareParticleTypeData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		return new SquareParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), this.spriteSet);
	}

	public static ParticleOptions createData(ParticleColor color) {
		return new SquareParticleTypeData(TransprotwoParticles.SQUARE_TYPE.get(), color);
	}

	public static ParticleOptions createData(double r, double g, double b) {
		return new SquareParticleTypeData(TransprotwoParticles.SQUARE_TYPE.get(), new ParticleColor(r, g, b));
	}
}