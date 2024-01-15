package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateDispatcherPayload(CompoundTag compound, BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Transprotwo.MOD_ID, "update_dispatcher");

	public UpdateDispatcherPayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(blockEntityPos);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
