package com.mrbysco.transprotwo.tile.transfer.power;

import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PowerTransfer extends AbstractTransfer {
	public PowerStack powerStack;

	private PowerTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public PowerTransfer(BlockPos dis, BlockPos rec, Direction face, PowerStack stack) {
		super(dis, rec, face);
		this.powerStack = stack;
	}

	public void readFromNBT(CompoundNBT compound) {
		CompoundNBT tag = compound.getCompound("power");
		powerStack = PowerStack.read(tag);
		super.readFromNBT(compound);
	}

	public CompoundNBT writeToNBT(CompoundNBT compound) {
		CompoundNBT tag = new CompoundNBT();
		powerStack.write(tag);
		compound.put("power", tag);
		return super.writeToNBT(compound);
	}

	public static PowerTransfer loadFromNBT(CompoundNBT nbt) {
		PowerTransfer transfer = new PowerTransfer();
		transfer.readFromNBT(nbt);
		return transfer;
	}
}
