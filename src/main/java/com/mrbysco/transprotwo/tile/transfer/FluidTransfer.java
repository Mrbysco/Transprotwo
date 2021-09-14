package com.mrbysco.transprotwo.tile.transfer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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

	public void readFromNBT(CompoundNBT compound) {
		CompoundNBT tag = compound.getCompound("fluidstack");
		fluidStack = FluidStack.loadFluidStackFromNBT(tag);
		super.readFromNBT(compound);
	}

	public CompoundNBT writeToNBT(CompoundNBT compound) {
		CompoundNBT tag = new CompoundNBT();
		fluidStack.writeToNBT(tag);
		compound.put("fluidstack", tag);
		return super.writeToNBT(compound);
	}

	public static FluidTransfer loadFromNBT(CompoundNBT nbt) {
		FluidTransfer transfer = new FluidTransfer();
		transfer.readFromNBT(nbt);
		return transfer;
	}
}
