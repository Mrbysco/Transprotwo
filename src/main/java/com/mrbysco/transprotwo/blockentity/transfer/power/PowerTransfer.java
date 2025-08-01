package com.mrbysco.transprotwo.blockentity.transfer.power;

import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class PowerTransfer extends AbstractTransfer {
	public PowerStack powerStack;

	private PowerTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public PowerTransfer(BlockPos dis, BlockPos rec, Direction face, PowerStack stack) {
		super(dis, rec, face);
		this.powerStack = stack;
	}

	public void readFromNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		CompoundTag tag = compound.getCompoundOrEmpty("power");
		powerStack = PowerStack.read(tag);
		super.readFromNBT(compound, lookupProvider);
	}

	public CompoundTag writeToNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		powerStack.write(tag);
		compound.put("power", tag);
		return super.writeToNBT(compound, lookupProvider);
	}

	public static PowerTransfer loadFromNBT(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		PowerTransfer transfer = new PowerTransfer();
		transfer.readFromNBT(nbt, lookupProvider);
		return transfer;
	}
}
