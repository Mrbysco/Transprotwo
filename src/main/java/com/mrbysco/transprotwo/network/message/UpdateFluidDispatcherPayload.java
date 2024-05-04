package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateFluidDispatcherPayload(CompoundTag compound,
                                           BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdateFluidDispatcherPayload> CODEC = CustomPacketPayload.codec(
			UpdateFluidDispatcherPayload::write,
			UpdateFluidDispatcherPayload::new);
	public static final Type<UpdateFluidDispatcherPayload> ID = CustomPacketPayload.createType(new ResourceLocation(Transprotwo.MOD_ID, "update_fluid_dispatcher").toString());

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
