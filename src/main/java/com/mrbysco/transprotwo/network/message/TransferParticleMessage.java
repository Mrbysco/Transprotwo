package com.mrbysco.transprotwo.network.message;

import com.mrbysco.transprotwo.client.particles.SquareParticleData;
import com.mrbysco.transprotwo.config.TransprotConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.Random;
import java.util.function.Supplier;

public class TransferParticleMessage {
	private final CompoundNBT compound;

	public TransferParticleMessage(CompoundNBT tag) {
		this.compound = tag;
	}

	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag(compound);
	}

	public static TransferParticleMessage decode(final PacketBuffer packetBuffer) {
		return new TransferParticleMessage(packetBuffer.readCompoundTag());
	}

	public void handle(Supplier<Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
				World world = mc.world;

				summonParticles(world, compound);
			}
		});
		ctx.setPacketHandled(true);
	}

	public void summonParticles(World world, CompoundNBT nbt) {
		if (!TransprotConfig.CLIENT.showParticles.get())
			return;
		BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
		Vector3d vec = new Vector3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
		double dx = vec.x, dy = vec.y, dz = vec.z;
		for (int i = 0; i < 7; i++) {
			double xx = (new Random().nextDouble() - .5) / 2.3;
			double yy = (new Random().nextDouble() - .5) / 2.3;
			double zz = (new Random().nextDouble() - .5) / 2.3;
			world.addParticle(SquareParticleData.createData(255, 136, 255),
					pos.getX() + .5 + xx, pos.getY() + .5 + yy, pos.getZ() + .5 + zz, dx, dy, dz);
		}
	}
}
