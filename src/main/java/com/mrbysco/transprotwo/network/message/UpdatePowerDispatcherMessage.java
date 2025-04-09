package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdatePowerDispatcherMessage(CompoundTag compound,
                                           BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdatePowerDispatcherMessage> CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			payload -> payload.compound,
			BlockPos.STREAM_CODEC,
			payload -> payload.blockEntityPos,
			UpdatePowerDispatcherMessage::new
	);
	public static final Type<UpdatePowerDispatcherMessage> ID = new Type<>(Transprotwo.modLoc("update_power_dispatcher"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
