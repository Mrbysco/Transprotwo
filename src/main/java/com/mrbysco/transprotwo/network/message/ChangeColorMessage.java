package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ChangeColorMessage {
	public BlockPos blockEntityPos;

	public ChangeColorMessage(BlockPos blockEntityPos) {
		this.blockEntityPos = blockEntityPos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(blockEntityPos);
	}

	public static ChangeColorMessage decode(final FriendlyByteBuf packetBuffer) {
		return new ChangeColorMessage(packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				ClientHelper.resetColors(blockEntityPos);
			}
		});
		ctx.setPacketHandled(true);
	}
}
