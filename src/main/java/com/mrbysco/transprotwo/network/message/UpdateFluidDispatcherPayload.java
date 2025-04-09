package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateFluidDispatcherPayload(CompoundTag compound,
                                           BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdateFluidDispatcherPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			payload -> payload.compound,
			BlockPos.STREAM_CODEC,
			payload -> payload.blockEntityPos,
			UpdateFluidDispatcherPayload::new
	);
	public static final Type<UpdateFluidDispatcherPayload> ID = new Type<>(Transprotwo.modLoc("update_fluid_dispatcher"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
