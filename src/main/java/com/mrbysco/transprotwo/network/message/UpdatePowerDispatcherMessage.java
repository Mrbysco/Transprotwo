package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdatePowerDispatcherMessage {
	private final CompoundNBT compound;
	public BlockPos tilePos;

	public UpdatePowerDispatcherMessage(CompoundNBT tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(PacketBuffer buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdatePowerDispatcherMessage decode(final PacketBuffer packetBuffer) {
		return new UpdatePowerDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayerEntity player = ctx.getSender();
				World world = player.level;
				TileEntity tile = world.getBlockEntity(tilePos);
				if(tile instanceof PowerDispatcherTile) {
					PowerDispatcherTile dispatcherTile = (PowerDispatcherTile) tile;
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
