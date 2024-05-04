package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdatePowerDispatcherMessage(CompoundTag compound,
                                           BlockPos blockEntityPos) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, UpdatePowerDispatcherMessage> CODEC = CustomPacketPayload.codec(
			UpdatePowerDispatcherMessage::write,
			UpdatePowerDispatcherMessage::new);
	public static final Type<UpdatePowerDispatcherMessage> ID = CustomPacketPayload.createType(new ResourceLocation(Transprotwo.MOD_ID, "update_power_dispatcher").toString());

	public UpdatePowerDispatcherMessage(final FriendlyByteBuf packetBuffer) {
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
