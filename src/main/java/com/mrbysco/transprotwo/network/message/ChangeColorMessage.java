package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.client.ClientHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ChangeColorMessage {
	public BlockPos tilePos;

	public ChangeColorMessage(BlockPos tilePos) {
		this.tilePos = tilePos;
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(tilePos);
	}

	public static ChangeColorMessage decode(final PacketBuffer packetBuffer) {
		return new ChangeColorMessage(packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				ClientHelper.resetColors(tilePos);
			}
		});
		ctx.setPacketHandled(true);
	}
}
