package com.mrbysco.transprotwo.network;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.network.message.ChangeColorMessage;
import com.mrbysco.transprotwo.network.message.TransferParticleMessage;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherMessage;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherMessage;
import com.mrbysco.transprotwo.network.message.UpdatePowerDispatcherMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Transprotwo.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init(){
		CHANNEL.registerMessage(id++, UpdateDispatcherMessage.class, UpdateDispatcherMessage::encode, UpdateDispatcherMessage::decode, UpdateDispatcherMessage::handle);
		CHANNEL.registerMessage(id++, UpdateFluidDispatcherMessage.class, UpdateFluidDispatcherMessage::encode, UpdateFluidDispatcherMessage::decode, UpdateFluidDispatcherMessage::handle);
		CHANNEL.registerMessage(id++, UpdatePowerDispatcherMessage.class, UpdatePowerDispatcherMessage::encode, UpdatePowerDispatcherMessage::decode, UpdatePowerDispatcherMessage::handle);
		CHANNEL.registerMessage(id++, ChangeColorMessage.class, ChangeColorMessage::encode, ChangeColorMessage::decode, ChangeColorMessage::handle);
		CHANNEL.registerMessage(id++, TransferParticleMessage.class, TransferParticleMessage::encode, TransferParticleMessage::decode, TransferParticleMessage::handle);
	}

	public static void sendToNearbyPlayers(Object message, BlockPos pos, double radius, ResourceKey<Level> dim) {
		CHANNEL.send(net.minecraftforge.network.PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), message);
	}
}
