package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdatePowerDispatcherMessage {
	private final CompoundTag compound;
	public BlockPos blockEntityPos;

	public UpdatePowerDispatcherMessage(CompoundTag tag, BlockPos blockEntityPos) {
		this.compound = tag;
		this.blockEntityPos = blockEntityPos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(blockEntityPos);
	}

	public static UpdatePowerDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdatePowerDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level level = player.level();
				BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
				if (blockEntity instanceof PowerDispatcherBE powerDispatcher) {
					if (compound.contains("mode"))
						powerDispatcher.cycleMode();
					if (compound.contains("reset"))
						powerDispatcher.resetOptions();
					if (compound.contains("color1"))
						powerDispatcher.setLine1(compound.getInt("color1"));
					if (compound.contains("color2"))
						powerDispatcher.setLine2(compound.getInt("color2"));
					if (compound.contains("color3"))
						powerDispatcher.setLine3(compound.getInt("color3"));
					if (compound.contains("color4"))
						powerDispatcher.setLine4(compound.getInt("color4"));
					if (compound.contains("color5"))
						powerDispatcher.setLine5(compound.getInt("color5"));
					powerDispatcher.refreshClient();
					PacketHandler.sendToNearbyPlayers(new ChangeColorMessage(blockEntityPos), blockEntityPos, 32, level.dimension());
				}

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
