package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class SquareParticleTypeData implements ParticleOptions {
	private ParticleType<SquareParticleTypeData> type;
	public static final Codec<SquareParticleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, SquareParticleTypeData::new));
	public Color color;
	@SuppressWarnings("deprecation")
	static final ParticleOptions.Deserializer<SquareParticleTypeData> DESERIALIZER = new ParticleOptions.Deserializer<SquareParticleTypeData>() {

		@Override
		public SquareParticleTypeData fromCommand(ParticleType<SquareParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			return new SquareParticleTypeData(type, Color.deserialize(reader.readString()));
		}

		@Override
		public SquareParticleTypeData fromNetwork(ParticleType<SquareParticleTypeData> type, FriendlyByteBuf buffer) {
			return new SquareParticleTypeData(type, Color.deserialize(buffer.readUtf()));
		}
	};

	public SquareParticleTypeData(ParticleType<SquareParticleTypeData> particleTypeData, Color color) {
		this.type = particleTypeData;
		this.color = color;
	}

	public SquareParticleTypeData(float r, float g, float b) {
		this(TransprotwoParticles.SQUARE_TYPE.get(), new Color(r, g, b));
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
	}

	@Override
	public String writeToString() {
		return null;
	}
}
