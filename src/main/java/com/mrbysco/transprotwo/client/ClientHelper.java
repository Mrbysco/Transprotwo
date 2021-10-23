package com.mrbysco.transprotwo.client;

import com.mrbysco.transprotwo.client.particles.SquareParticleData;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class ClientHelper {
	public static void resetColors(BlockPos pos) {
		net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		net.minecraft.world.World world = mc.level;
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof PowerDispatcherTile) {
			PowerDispatcherTile dispatcherTile = (PowerDispatcherTile) tile;
			dispatcherTile.initializeColors();
		}
	}

	public static void summonParticles(CompoundNBT compound) {
		net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		net.minecraft.world.World world = mc.level;

		summonParticles(world, compound);
	}

	public static void summonParticles(World world, CompoundNBT nbt) {
		if (!TransprotConfig.CLIENT.showParticles.get())
			return;
		BlockPos pos = BlockPos.of(nbt.getLong("pos"));
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
