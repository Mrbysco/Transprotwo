package com.mrbysco.transprotwo.network.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class TransferParticleMessage {
	private final CompoundNBT compound;

	public TransferParticleMessage(CompoundNBT tag) {
		this.compound = tag;
	}

	public void encode(PacketBuffer buf) {
		buf.writeNbt(compound);
	}

	public static TransferParticleMessage decode(final PacketBuffer packetBuffer) {
		return new TransferParticleMessage(packetBuffer.readNbt());
	}

	public void handle(Supplier<Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				com.mrbysco.transprotwo.client.ClientHelper.summonParticles(compound);
			}
		});
		ctx.setPacketHandled(true);
	}
}
