package com.mrbysco.transprotwo.client.particles;

import com.mrbysco.transprotwo.client.particles.factory.ParticleColor;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleTypeData;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class SquareParticleData implements IParticleFactory<SquareParticleTypeData> {
	private final IAnimatedSprite spriteSet;

	public SquareParticleData(IAnimatedSprite sprite) {
		this.spriteSet = sprite;
	}

	@Override
	public Particle makeParticle(SquareParticleTypeData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		return new SquareParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), this.spriteSet);
	}

	public static IParticleData createData(ParticleColor color) {
		return new SquareParticleTypeData(TransprotwoParticles.SQUARE_TYPE.get(), color);
	}

	public static IParticleData createData(double r, double g, double b) {
		return new SquareParticleTypeData(TransprotwoParticles.SQUARE_TYPE.get(), new ParticleColor(r, g, b));
	}
}