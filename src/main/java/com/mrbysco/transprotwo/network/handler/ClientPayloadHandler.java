package com.mrbysco.transprotwo.network.handler;

import com.mrbysco.transprotwo.client.ClientHelper;
import com.mrbysco.transprotwo.network.message.ChangeColorPayload;
import com.mrbysco.transprotwo.network.message.TransferParticlePayload;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleColorPayload(final ChangeColorPayload colorPayload, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					ClientHelper.resetColors(colorPayload.blockEntityPos());
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("transprotwo.networking.change_color.failed", e.getMessage()));
					return null;
				});
	}

	public void handleParticlePayload(final TransferParticlePayload particlePayload, final PlayPayloadContext context) {
		context.workHandler().submitAsync(() -> {
					ClientHelper.summonParticles(particlePayload.compound());
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("transprotwo.networking.transfer_particle.failed", e.getMessage()));
					return null;
				});
	}
}
