package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
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
	public BlockPos blockEntityPos;

	public UpdateFluidDispatcherMessage(CompoundTag tag, BlockPos blockEntityPos) {
		this.compound = tag;
		this.blockEntityPos = blockEntityPos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(blockEntityPos);
	}

	public static UpdateFluidDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdateFluidDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level level = player.level();
				BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
				if (blockEntity instanceof FluidDispatcherBE fluidDispatcher) {
					if (compound.contains("mode"))
						fluidDispatcher.cycleMode();
					if (compound.contains("white"))
						fluidDispatcher.toggleWhite();
					if (compound.contains("reset"))
						fluidDispatcher.resetOptions();
					if (compound.contains("mod"))
						fluidDispatcher.toggleMod();

					fluidDispatcher.refreshClient();
				}

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
