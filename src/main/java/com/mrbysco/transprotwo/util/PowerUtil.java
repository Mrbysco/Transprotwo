package com.mrbysco.transprotwo.util;

import com.mrbysco.transprotwo.tile.transfer.power.PowerStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerUtil {
	public static boolean hasEnergyStorage(TileEntity tile, Direction side) {
		if (tile == null)
			return false;
		return tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent() || tile instanceof IInventory;
	}

	public static boolean hasEnergyStorage(IBlockReader world, BlockPos pos, Direction side) {
		return hasEnergyStorage(world.getBlockEntity(pos), side);
	}

	public static IEnergyStorage getEnergyStorage(TileEntity tile, Direction side) {
		if (tile == null)
			return null;
		if (tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent())
			return tile.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
		return null;
	}

	public static PowerStack insert(TileEntity destTile, PowerStack powerStack, Direction side) {
		if (destTile == null)
			return powerStack;
		IEnergyStorage destHandler = getEnergyStorage(destTile, side);
		int energyAmount = powerStack.getAmount();
		int amountFilled = destHandler.receiveEnergy(powerStack.getAmount(), false);
		if(amountFilled == energyAmount || amountFilled < 0) {
			return PowerStack.EMPTY;
		} else {
			return new PowerStack(energyAmount - amountFilled);
		}
	}

	public static int canInsert(IEnergyStorage destHandler, PowerStack stack) {
		if (destHandler == null || stack.isEmpty())
			return 0;
		return destHandler.receiveEnergy(stack.getAmount(), true);
	}
}
