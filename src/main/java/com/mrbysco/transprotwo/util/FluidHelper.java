package com.mrbysco.transprotwo.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidHelper {
	public static boolean hasFluidHandler(TileEntity tile, Direction side) {
		if (tile == null)
			return false;
		return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).isPresent() || tile instanceof IInventory;
	}

	public static boolean hasFluidHandler(IBlockReader world, BlockPos pos, Direction side) {
		return hasFluidHandler(world.getTileEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(TileEntity tile, Direction side) {
		if (tile == null)
			return null;
		if (tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).isPresent())
			return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).orElse(null);
		return null;
	}

	public static FluidStack insert(TileEntity destTile, FluidStack fluidStack, Direction side) {
		if (destTile == null)
			return fluidStack;
		IFluidHandler destHandler = getFluidHandler(destTile, side);
		int fluidAmount = fluidStack.getAmount();
		Fluid fluid = fluidStack.getFluid();
		int amountFilled = destHandler.fill(fluidStack, FluidAction.EXECUTE);
		if(amountFilled == fluidAmount) {
			return FluidStack.EMPTY;
		} else {
			return new FluidStack(fluid, fluidAmount - amountFilled);
		}
	}

	public static int canInsert(IFluidHandler destHandler, FluidStack stack) {
		if (destHandler == null || stack.isEmpty())
			return 0;
		return destHandler.fill(stack, FluidAction.SIMULATE);
	}
}
