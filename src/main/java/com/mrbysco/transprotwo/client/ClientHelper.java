package com.mrbysco.transprotwo.client;

import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.client.particles.SquareParticleData;
import com.mrbysco.transprotwo.config.TransprotConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ClientHelper {
	public static void resetColors(BlockPos pos) {
		net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		net.minecraft.world.level.Level level = mc.level;
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof PowerDispatcherBE powerDispatcher) {
			powerDispatcher.initializeColors();
		}
	}

	public static void summonParticles(CompoundTag compound) {
		net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		net.minecraft.world.level.Level level = mc.level;

		summonParticles(level, compound);
	}

	public static void summonParticles(Level level, CompoundTag nbt) {
		if (!TransprotConfig.CLIENT.showParticles.get())
			return;
		BlockPos pos = BlockPos.of(nbt.getLong("pos"));
		Vec3 vec = new Vec3(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
		double dx = vec.x, dy = vec.y, dz = vec.z;
		for (int i = 0; i < 7; i++) {
			double xx = (new Random().nextDouble() - .5) / 2.3;
			double yy = (new Random().nextDouble() - .5) / 2.3;
			double zz = (new Random().nextDouble() - .5) / 2.3;
			level.addParticle(SquareParticleData.createData(255, 136, 255),
					pos.getX() + .5 + xx, pos.getY() + .5 + yy, pos.getZ() + .5 + zz, dx, dy, dz);
		}
	}
}
