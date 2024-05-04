package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateDispatcherPayload(CompoundTag compound, BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdateDispatcherPayload> CODEC = CustomPacketPayload.codec(
			UpdateDispatcherPayload::write,
			UpdateDispatcherPayload::new);
	public static final Type<UpdateDispatcherPayload> ID = CustomPacketPayload.createType(new ResourceLocation(Transprotwo.MOD_ID, "update_dispatcher").toString());

	public UpdateDispatcherPayload(final FriendlyByteBuf packetBuffer) {
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
