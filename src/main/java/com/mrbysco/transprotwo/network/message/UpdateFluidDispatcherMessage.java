package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.tile.FluidDispatcherBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class UpdateFluidDispatcherMessage {
	private final CompoundTag compound;
	public BlockPos tilePos;

	public UpdateFluidDispatcherMessage(CompoundTag tag, BlockPos tilePos) {
		this.compound = tag;
		this.tilePos = tilePos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(tilePos);
	}

	public static UpdateFluidDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdateFluidDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level world = player.level;
				BlockEntity tile = world.getBlockEntity(tilePos);
				if(tile instanceof FluidDispatcherBE dispatcherTile) {
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

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
