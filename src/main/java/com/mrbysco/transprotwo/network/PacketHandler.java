package com.mrbysco.transprotwo.network;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.network.handler.ClientPayloadHandler;
import com.mrbysco.transprotwo.network.handler.ServerPayloadHandler;
import com.mrbysco.transprotwo.network.message.ChangeColorPayload;
import com.mrbysco.transprotwo.network.message.TransferParticlePayload;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherPayload;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherPayload;
import com.mrbysco.transprotwo.network.message.UpdatePowerDispatcherMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(Transprotwo.MOD_ID);
		registrar.playToClient(ChangeColorPayload.ID, ChangeColorPayload.CODEC, ClientPayloadHandler.getInstance()::handleColorPayload);
		registrar.playToClient(TransferParticlePayload.ID, TransferParticlePayload.CODEC, ClientPayloadHandler.getInstance()::handleParticlePayload);
		registrar.playToServer(UpdateDispatcherPayload.ID, UpdateDispatcherPayload.CODEC, ServerPayloadHandler.getInstance()::handleDispatcherPayload);
		registrar.playToServer(UpdateFluidDispatcherPayload.ID, UpdateFluidDispatcherPayload.CODEC, ServerPayloadHandler.getInstance()::handleFluidDispatcherPayload);
		registrar.playToServer(UpdatePowerDispatcherMessage.ID, UpdatePowerDispatcherMessage.CODEC, ServerPayloadHandler.getInstance()::handlePowerDispatcherPayload);
	}

	public static void sendToNearbyPlayers(CustomPacketPayload payload, BlockPos pos, double radius, Level level) {
		if (level instanceof ServerLevel serverLevel)
			PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), radius, payload);
		else
			throw new IllegalStateException("Cannot send packets to nearby players using a client world.");
	}
}
