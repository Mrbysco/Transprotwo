package com.mrbysco.transprotwo.network.handler;

import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.ChangeColorPayload;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherPayload;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherPayload;
import com.mrbysco.transprotwo.network.message.UpdatePowerDispatcherMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleDispatcherPayload(final UpdateDispatcherPayload updateDispatcherPayload, final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.enqueueWork(() -> {
					if (context.player() != null) {
						Player player = context.player();
						Level level = player.level();
						BlockEntity blockEntity = level.getBlockEntity(updateDispatcherPayload.blockEntityPos());
						if (blockEntity instanceof ItemDispatcherBE itemDispatcher) {
							CompoundTag compound = updateDispatcherPayload.compound();
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

						player.containerMenu.slotsChanged(null);
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("transprotwo.networking.update_dispatcher.failed", e.getMessage()));
					return null;
				});
	}

	public void handleFluidDispatcherPayload(final UpdateFluidDispatcherPayload updateDispatcherPayload, final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.enqueueWork(() -> {
					if (context.player() != null) {
						Player player = context.player();
						Level level = player.level();
						BlockEntity blockEntity = level.getBlockEntity(updateDispatcherPayload.blockEntityPos());
						if (blockEntity instanceof FluidDispatcherBE fluidDispatcher) {
							CompoundTag compound = updateDispatcherPayload.compound();
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

						player.containerMenu.slotsChanged(null);
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("transprotwo.networking.update_fluid_dispatcher.failed", e.getMessage()));
					return null;
				});
	}

	public void handlePowerDispatcherPayload(final UpdatePowerDispatcherMessage updateDispatcherPayload, final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.enqueueWork(() -> {
					if (context.player() != null) {
						Player player = context.player();
						Level level = player.level();
						BlockPos blockEntityPos = updateDispatcherPayload.blockEntityPos();
						BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
						if (blockEntity instanceof PowerDispatcherBE powerDispatcher) {
							CompoundTag compound = updateDispatcherPayload.compound();
							if (compound.contains("mode"))
								powerDispatcher.cycleMode();
							if (compound.contains("reset"))
								powerDispatcher.resetOptions();
							if (compound.contains("color1"))
								powerDispatcher.setLine1(compound.getIntOr("color1", 0));
							if (compound.contains("color2"))
								powerDispatcher.setLine2(compound.getIntOr("color2", 0));
							if (compound.contains("color3"))
								powerDispatcher.setLine3(compound.getIntOr("color3", 0));
							if (compound.contains("color4"))
								powerDispatcher.setLine4(compound.getIntOr("color4", 0));
							if (compound.contains("color5"))
								powerDispatcher.setLine5(compound.getIntOr("color5", 0));
							powerDispatcher.refreshClient();
							PacketHandler.sendToNearbyPlayers(new ChangeColorPayload(blockEntityPos), blockEntityPos, 32, level);
						}

						player.containerMenu.slotsChanged(null);
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("transprotwo.networking.update_power_dispatcher.failed", e.getMessage()));
					return null;
				});
	}
}
