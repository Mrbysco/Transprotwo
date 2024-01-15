package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChangeColorPayload(BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Transprotwo.MOD_ID, "change_color");

	public ChangeColorPayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readBlockPos());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(blockEntityPos);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
