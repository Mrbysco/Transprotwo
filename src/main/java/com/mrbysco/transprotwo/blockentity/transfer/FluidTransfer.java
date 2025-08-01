package com.mrbysco.transprotwo.blockentity.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTransfer extends AbstractTransfer {
	public FluidStack fluidStack;

	private FluidTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public FluidTransfer(BlockPos dis, BlockPos rec, Direction face, FluidStack stack) {
		super(dis, rec, face);
		this.fluidStack = stack;
	}

	public void readFromNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		fluidStack = FluidStack.parseOptional(lookupProvider, compound.getCompoundOrEmpty("fluidstack"));
		super.readFromNBT(compound, lookupProvider);
	}

	public CompoundTag writeToNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		compound.put("fluidstack", fluidStack.saveOptional(lookupProvider));
		return super.writeToNBT(compound, lookupProvider);
	}

	public static FluidTransfer loadFromNBT(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		FluidTransfer transfer = new FluidTransfer();
		transfer.readFromNBT(nbt, lookupProvider);
		return transfer;
	}
}
