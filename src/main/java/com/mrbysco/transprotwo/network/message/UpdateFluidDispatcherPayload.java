package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateFluidDispatcherPayload(CompoundTag compound,
                                           BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdateFluidDispatcherPayload> CODEC = CustomPacketPayload.codec(
			UpdateFluidDispatcherPayload::write,
			UpdateFluidDispatcherPayload::new);
	public static final Type<UpdateFluidDispatcherPayload> ID = new Type<>(Transprotwo.modLoc("update_fluid_dispatcher"));

	public UpdateFluidDispatcherPayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readNbt(), packetBuffer.readBlockPos());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(compound);
		buf.writeBlockPos(blockEntityPos);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
