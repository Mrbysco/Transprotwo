package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SquareParticleTypeData implements ParticleOptions {
	private final ParticleType<SquareParticleTypeData> type;
	public static final MapCodec<SquareParticleTypeData> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							Color.CODEC.fieldOf("color").forGetter(p_253371_ -> p_253371_.color)
					)
					.apply(instance, SquareParticleTypeData::new)
	);
	public final Color color;

	public static final StreamCodec<RegistryFriendlyByteBuf, SquareParticleTypeData> STREAM_CODEC = StreamCodec.composite(
			Color.STREAM_CODEC, p_319429_ -> p_319429_.color, SquareParticleTypeData::new
	);

	public SquareParticleTypeData(ParticleType<SquareParticleTypeData> particleTypeData, Color color) {
		this.type = particleTypeData;
		this.color = color;
	}

	public SquareParticleTypeData(float r, float g, float b, float a) {
		this(TransprotwoParticles.SQUARE_TYPE.get(), new Color(r, g, b, a));
	}

	public SquareParticleTypeData(Color vector3f) {
		this(TransprotwoParticles.SQUARE_TYPE.get(), vector3f);
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}
}
