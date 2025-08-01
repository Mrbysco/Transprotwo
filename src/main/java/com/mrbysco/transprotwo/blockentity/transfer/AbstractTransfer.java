package com.mrbysco.transprotwo.blockentity.transfer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTransfer {
	public BlockPos dis;
	public Pair<BlockPos, Direction> rec;
	public Vec3 current, prev;
	public boolean blocked = false;
	public int turn;

	private AbstractTransfer() {
	}

	public AbstractTransfer(BlockPos dis, Pair<BlockPos, Direction> rec, Vec3 current, boolean blocked, int turn) {
		this.dis = dis;
		this.rec = rec;
		this.current = current;
		this.blocked = blocked;
		this.turn = turn;
	}

	public boolean received() {
		// return current.lengthVector() > getVec().lengthVector();
		double distance = new Vec3(dis.getX(), dis.getY(), dis.getZ())
				.add(current).distanceTo(new Vec3(rec.getFirst().getX() + .5, rec.getFirst().getY() + .5, rec.getFirst().getZ() + .5));
		return distance < .5;
	}

	public Vec3 getVec() {
		return new Vec3(rec.getFirst().getX() - dis.getX(), rec.getFirst().getY() - dis.getY(), rec.getFirst().getZ() - dis.getZ());
	}
}
