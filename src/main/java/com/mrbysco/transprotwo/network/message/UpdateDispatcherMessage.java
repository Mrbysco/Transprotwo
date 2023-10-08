package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
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
	public BlockPos blockEntityPos;

	public UpdateDispatcherMessage(CompoundTag tag, BlockPos blockEntityPos) {
		this.compound = tag;
		this.blockEntityPos = blockEntityPos;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(blockEntityPos);
	}

	public static UpdateDispatcherMessage decode(final FriendlyByteBuf packetBuffer) {
		return new UpdateDispatcherMessage(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				Level level = player.level();
				BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
				if (blockEntity instanceof ItemDispatcherBE itemDispatcher) {
					if (compound.contains("mode"))
						itemDispatcher.cycleMode();
					if (compound.contains("tag"))
						itemDispatcher.toggleTag();
					if (compound.contains("durability"))
						itemDispatcher.toggleDurability();
					if (compound.contains("nbt"))
						itemDispatcher.toggleNbt();
					if (compound.contains("white"))
						itemDispatcher.toggleWhite();
					if (compound.contains("reset"))
						itemDispatcher.resetOptions();
					if (compound.contains("mod"))
						itemDispatcher.toggleMod();
					if (compound.contains("stockUp"))
						itemDispatcher.incrementStockNum();
					if (compound.contains("stockDown"))
						itemDispatcher.decreaseStockNum();

					itemDispatcher.refreshClient();
				}

				ctx.getSender().containerMenu.slotsChanged(null);
			}
		});
		ctx.setPacketHandled(true);
	}
}
