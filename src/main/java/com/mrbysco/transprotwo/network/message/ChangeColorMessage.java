package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ChangeColorMessage {
	public BlockPos tilePos;

	public ChangeColorMessage(BlockPos tilePos) {
		this.tilePos = tilePos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(tilePos);
	}

	public static ChangeColorMessage decode(final FriendlyByteBuf packetBuffer) {
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
