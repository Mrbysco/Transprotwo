package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChangeColorPayload(BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ChangeColorPayload> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			payload -> payload.blockEntityPos,
			ChangeColorPayload::new
	);
	public static final Type<ChangeColorPayload> ID = new Type<>(Transprotwo.modLoc("change_color"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
