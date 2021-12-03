package com.mrbysco.transprotwo.tile.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public class FluidTransfer extends AbstractTransfer {
	public FluidStack fluidStack;

	private FluidTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public FluidTransfer(BlockPos dis, BlockPos rec, Direction face, FluidStack stack) {
		super(dis, rec, face);
		this.fluidStack = stack;
	}

	public void readFromNBT(CompoundTag compound) {
		CompoundTag tag = compound.getCompound("fluidstack");
		fluidStack = FluidStack.loadFluidStackFromNBT(tag);
		super.readFromNBT(compound);
	}

	public CompoundTag writeToNBT(CompoundTag compound) {
		CompoundTag tag = new CompoundTag();
		fluidStack.writeToNBT(tag);
		compound.put("fluidstack", tag);
		return super.writeToNBT(compound);
	}

	public static FluidTransfer loadFromNBT(CompoundTag nbt) {
		FluidTransfer transfer = new FluidTransfer();
		transfer.readFromNBT(nbt);
		return transfer;
	}
}
