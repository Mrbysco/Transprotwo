package com.mrbysco.transprotwo.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent.Context;

public class TransferParticleMessage {
	private final CompoundTag compound;

	public TransferParticleMessage(CompoundTag tag) {
		this.compound = tag;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
	}

	public static TransferParticleMessage decode(final FriendlyByteBuf packetBuffer) {
		return new TransferParticleMessage(packetBuffer.readNbt());
	}

	public void handle(Context ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				com.mrbysco.transprotwo.client.ClientHelper.summonParticles(compound);
			}
		});
		ctx.setPacketHandled(true);
	}
}
