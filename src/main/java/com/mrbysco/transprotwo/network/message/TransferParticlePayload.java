package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TransferParticlePayload(CompoundTag compound) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Transprotwo.MOD_ID, "transfer_particle");

	public TransferParticlePayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readNbt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
	}

	public static TransferParticlePayload decode(final FriendlyByteBuf packetBuffer) {
		return new TransferParticlePayload(packetBuffer.readNbt());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
