package com.mrbysco.transprotwo.util;

import com.mrbysco.transprotwo.blockentity.transfer.power.PowerStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class PowerUtil {

	public static boolean hasEnergyStorage(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side) != null;
	}

	public static IEnergyStorage getEnergyStorage(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
	}

	public static PowerStack insert(Level level, BlockPos pos, PowerStack powerStack, Direction side) {
		IEnergyStorage destHandler = getEnergyStorage(level, pos, side);
		int energyAmount = powerStack.getAmount();
		int amountFilled = destHandler.receiveEnergy(powerStack.getAmount(), false);
		if (amountFilled == energyAmount || amountFilled < 0) {
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
