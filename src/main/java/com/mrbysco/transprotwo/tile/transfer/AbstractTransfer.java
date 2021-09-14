package com.mrbysco.transprotwo.tile.transfer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

public abstract class AbstractTransfer {
	public BlockPos dis;
	public Pair<BlockPos, Direction> rec;
	public Vector3d current, prev;
	public boolean blocked = false;
	public int turn;

	private AbstractTransfer() {
	}

	public void readFromNBT(CompoundNBT compound) {
		dis = BlockPos.fromLong(compound.getLong("dis"));
		rec = new ImmutablePair<>(BlockPos.fromLong(compound.getLong("rec")), Direction.values()[compound.getInt("face")]);
		current = new Vector3d(compound.getDouble("xx"), compound.getDouble("yy"), compound.getDouble("zz"));
		blocked = compound.getBoolean("blocked");
		turn = compound.getInt("turn");
	}

	public CompoundNBT writeToNBT(CompoundNBT compound) {
		compound.putLong("dis", dis.toLong());
		compound.putLong("rec", rec.getLeft().toLong());
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
		this.current = new Vector3d(.5, .5, .5);
		this.turn = new Random().nextInt();
	}

	public boolean received() {
		// return current.lengthVector() > getVec().lengthVector();
		double distance = new Vector3d(dis.getX(), dis.getY(), dis.getZ()).add(current).distanceTo(new Vector3d(rec.getLeft().getX() + .5, rec.getLeft().getY() + .5, rec.getLeft().getZ() + .5));
		return distance < .5;
	}

	public Vector3d getVec() {
		return new Vector3d(rec.getLeft().getX() - dis.getX(), rec.getLeft().getY() - dis.getY(), rec.getLeft().getZ() - dis.getZ());
	}
}
