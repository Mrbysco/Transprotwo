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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(Transprotwo.MOD_ID);
		registrar.play(ChangeColorPayload.ID, ChangeColorPayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleColorPayload));
		registrar.play(TransferParticlePayload.ID, TransferParticlePayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleParticlePayload));
		registrar.play(UpdateDispatcherPayload.ID, UpdateDispatcherPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleDispatcherPayload));
		registrar.play(UpdateFluidDispatcherPayload.ID, UpdateFluidDispatcherPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleFluidDispatcherPayload));
		registrar.play(UpdatePowerDispatcherMessage.ID, UpdatePowerDispatcherMessage::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handlePowerDispatcherPayload));
	}

	public static void sendToNearbyPlayers(CustomPacketPayload payload, BlockPos pos, double radius, ResourceKey<Level> dim) {
		PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, dim)).send(payload);
	}
}
