package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdateFluidDispatcherMessage {
	private final CompoundNBT compound;
	public BlockPos tilePos;

	public UpdateFluidDispatcherMessage(CompoundNBT tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdateFluidDispatcherMessage decode(final PacketBuffer packetBuffer) {
		return new UpdateFluidDispatcherMessage(packetBuffer.readCompoundTag(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayerEntity player = ctx.getSender();
				World world = player.world;
				TileEntity tile = world.getTileEntity(tilePos);
				if(tile instanceof FluidDispatcherTile) {
					FluidDispatcherTile dispatcherTile = (FluidDispatcherTile) tile;
					if (compound.contains("mode"))
						dispatcherTile.cycleMode();
					if(compound.contains("white"))
						dispatcherTile.toggleWhite();
					if(compound.contains("reset"))
						dispatcherTile.resetOptions();
					if(compound.contains("mod"))
						dispatcherTile.toggleMod();

					dispatcherTile.refreshClient();
				}

				ctx.getSender().openContainer.onCraftMatrixChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
