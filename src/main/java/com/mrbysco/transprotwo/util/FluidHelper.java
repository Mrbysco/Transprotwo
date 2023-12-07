package com.mrbysco.transprotwo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHelper {
	public static boolean hasFluidHandler(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return false;
		return blockEntity.getCapability(Capabilities.FLUID_HANDLER, side).isPresent() || blockEntity instanceof Container;
	}

	public static boolean hasFluidHandler(BlockGetter world, BlockPos pos, Direction side) {
		return hasFluidHandler(world.getBlockEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return null;
		if (blockEntity.getCapability(Capabilities.FLUID_HANDLER, side).isPresent())
			return blockEntity.getCapability(Capabilities.FLUID_HANDLER, side).orElse(null);
		return null;
	}

	public static FluidStack insert(BlockEntity destBlockEntity, FluidStack fluidStack, Direction side) {
		if (destBlockEntity == null)
			return fluidStack;
		IFluidHandler destHandler = getFluidHandler(destBlockEntity, side);
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
