package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.serialization.Codec;
import net.minecraft.particles.ParticleType;

public class SquareParticleType extends ParticleType<SquareParticleTypeData> {

	public SquareParticleType() {
		super(false, SquareParticleTypeData.DESERIALIZER);
	}

	@Override
	public Codec<SquareParticleTypeData> codec() {
		return SquareParticleTypeData.CODEC;
	}
}
