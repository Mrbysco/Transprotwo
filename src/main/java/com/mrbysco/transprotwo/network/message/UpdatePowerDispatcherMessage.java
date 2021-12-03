package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.tile.PowerDispatcherBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdatePowerDispatcherMessage {
	private final CompoundTag compound;
	public BlockPos tilePos;

	public UpdatePowerDispatcherMessage(CompoundTag tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdatePowerDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdatePowerDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level world = player.level;
				BlockEntity tile = world.getBlockEntity(tilePos);
				if(tile instanceof PowerDispatcherBE dispatcherTile) {
					if (compound.contains("mode"))
						dispatcherTile.cycleMode();
					if(compound.contains("reset"))
						dispatcherTile.resetOptions();
					if(compound.contains("color1"))
						dispatcherTile.setLine1(compound.getInt("color1"));
					if(compound.contains("color2"))
						dispatcherTile.setLine2(compound.getInt("color2"));
					if(compound.contains("color3"))
						dispatcherTile.setLine3(compound.getInt("color3"));
					if(compound.contains("color4"))
						dispatcherTile.setLine4(compound.getInt("color4"));
					if(compound.contains("color5"))
						dispatcherTile.setLine5(compound.getInt("color5"));
					dispatcherTile.refreshClient();
					PacketHandler.sendToNearbyPlayers(new ChangeColorMessage(tilePos), tilePos, 32, world.dimension());
				}

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
