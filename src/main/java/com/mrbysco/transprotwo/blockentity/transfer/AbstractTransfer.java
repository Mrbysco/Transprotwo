package com.mrbysco.transprotwo.blockentity.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

public abstract class AbstractTransfer {
	public BlockPos dis;
	public Pair<BlockPos, Direction> rec;
	public Vec3 current, prev;
	public boolean blocked = false;
	public int turn;

	private AbstractTransfer() {
	}

	public void readFromNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		dis = BlockPos.of(compound.getLongOr("dis", 0L));
		rec = new ImmutablePair<>(BlockPos.of(compound.getLongOr("rec", 0L)), Direction.values()[compound.getIntOr("face", 0)]);
		current = new Vec3(compound.getDoubleOr("xx", 0), compound.getDoubleOr("yy", 0), compound.getDoubleOr("zz", 0));
		blocked = compound.getBooleanOr("blocked", false);
		turn = compound.getIntOr("turn", 0);
	}

	public CompoundTag writeToNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		compound.putLong("dis", dis.asLong());
		compound.putLong("rec", rec.getLeft().asLong());
		compound.putInt("face", rec.getRight().ordinal());
		compound.putDouble("xx", current.x);
		compound.putDouble("yy", current.y);
		compound.putDouble("zz", current.z);
		compound.putBoolean("blocked", blocked);
		compound.putInt("turn", turn);
		return compound;
	}

	public AbstractTransfer(BlockPos dis, BlockPos rec, Direction face) {
		this.dis = dis;
		this.rec = new ImmutablePair<>(rec, face);
		this.current = new Vec3(.5, .5, .5);
		this.turn = new Random().nextInt();
	}

	public boolean received() {
		// return current.lengthVector() > getVec().lengthVector();
		double distance = new Vec3(dis.getX(), dis.getY(), dis.getZ()).add(current).distanceTo(new Vec3(rec.getLeft().getX() + .5, rec.getLeft().getY() + .5, rec.getLeft().getZ() + .5));
		return distance < .5;
	}

	public Vec3 getVec() {
		return new Vec3(rec.getLeft().getX() - dis.getX(), rec.getLeft().getY() - dis.getY(), rec.getLeft().getZ() - dis.getZ());
	}
}
