package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.tile.ItemDispatcherTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdateDispatcherMessage {
	private final CompoundNBT compound;
	public BlockPos tilePos;

	public UpdateDispatcherMessage(CompoundNBT tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(PacketBuffer buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdateDispatcherMessage decode(final PacketBuffer packetBuffer) {
		return new UpdateDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayerEntity player = ctx.getSender();
				World world = player.level;
				TileEntity tile = world.getBlockEntity(tilePos);
				if(tile instanceof ItemDispatcherTile) {
					ItemDispatcherTile dispatcherTile = (ItemDispatcherTile) tile;
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
