package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.tile.ItemDispatcherBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdateDispatcherMessage {
	private final CompoundTag compound;
	public BlockPos tilePos;

	public UpdateDispatcherMessage(CompoundTag tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdateDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdateDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level world = player.level;
				BlockEntity tile = world.getBlockEntity(tilePos);
				if(tile instanceof ItemDispatcherBE dispatcherTile) {
					if (compound.contains("mode"))
						dispatcherTile.cycleMode();
					if(compound.contains("tag"))
						dispatcherTile.toggleTag();
					if(compound.contains("durability"))
						dispatcherTile.toggleDurability();
					if(compound.contains("nbt"))
						dispatcherTile.toggleNbt();
					if(compound.contains("white"))
						dispatcherTile.toggleWhite();
					if(compound.contains("reset"))
						dispatcherTile.resetOptions();
					if(compound.contains("mod"))
						dispatcherTile.toggleMod();
					if(compound.contains("stockUp"))
						dispatcherTile.incrementStockNum();
					if(compound.contains("stockDown"))
						dispatcherTile.decreaseStockNum();

					dispatcherTile.refreshClient();
				}

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
