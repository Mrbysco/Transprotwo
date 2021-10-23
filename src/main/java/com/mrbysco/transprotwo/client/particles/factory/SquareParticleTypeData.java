package com.mrbysco.transprotwo.client.particles.factory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

public class SquareParticleTypeData implements IParticleData {
	private ParticleType<SquareParticleTypeData> type;
	public static final Codec<SquareParticleTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, SquareParticleTypeData::new));
	public ParticleColor color;
	@SuppressWarnings("deprecation")
	static final IParticleData.IDeserializer<SquareParticleTypeData> DESERIALIZER = new IParticleData.IDeserializer<SquareParticleTypeData>() {

		@Override
		public SquareParticleTypeData fromCommand(ParticleType<SquareParticleTypeData> type, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			return new SquareParticleTypeData(type, ParticleColor.deserialize(reader.readString()));
		}

		@Override
		public SquareParticleTypeData fromNetwork(ParticleType<SquareParticleTypeData> type, PacketBuffer buffer) {
			return new SquareParticleTypeData(type, ParticleColor.deserialize(buffer.readUtf()));
		}
	};

	public SquareParticleTypeData(ParticleType<SquareParticleTypeData> particleTypeData, ParticleColor color) {
		this.type = particleTypeData;
		this.color = color;
	}

	public SquareParticleTypeData(float r, float g, float b) {
		this(TransprotwoParticles.SQUARE_TYPE.get(), new ParticleColor(r, g, b));
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}

	@Override
	public void writeToNetwork(PacketBuffer buffer) {}

	@Override
	public String writeToString() {
		return null;
	}
}
