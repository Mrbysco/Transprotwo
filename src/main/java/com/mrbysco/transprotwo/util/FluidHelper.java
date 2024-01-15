package com.mrbysco.transprotwo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHelper {

	public static boolean hasFluidHandler(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side) != null;
	}

	public static IFluidHandler getFluidHandler(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
	}

	public static FluidStack insert(Level level, BlockPos pos, FluidStack fluidStack, Direction side) {
		IFluidHandler destHandler = getFluidHandler(level, pos, side);
		int fluidAmount = fluidStack.getAmount();
		Fluid fluid = fluidStack.getFluid();
		int amountFilled = destHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
		if (amountFilled == fluidAmount) {
			return FluidStack.EMPTY;
		} else {
			return new FluidStack(fluid, fluidAmount - amountFilled);
		}
	}

	public static int canInsert(IFluidHandler destHandler, FluidStack stack) {
		if (destHandler == null || stack.isEmpty())
			return 0;
		return destHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE);
	}
}
