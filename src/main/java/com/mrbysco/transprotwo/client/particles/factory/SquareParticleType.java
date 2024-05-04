package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SquareParticleType extends ParticleType<SquareParticleTypeData> {

	public SquareParticleType() {
		super(false);
	}

	@Override
	public MapCodec<SquareParticleTypeData> codec() {
		return SquareParticleTypeData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, SquareParticleTypeData> streamCodec() {
		return SquareParticleTypeData.STREAM_CODEC;
	}
}
