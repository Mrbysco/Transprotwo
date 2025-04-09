package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TransferParticlePayload(CompoundTag compound) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, TransferParticlePayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			payload -> payload.compound,
			TransferParticlePayload::new
	);
	public static final Type<TransferParticlePayload> ID = new Type<>(Transprotwo.modLoc("transfer_particle"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
