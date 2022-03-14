package com.mrbysco.transprotwo.util;

import com.mrbysco.transprotwo.blockentity.transfer.power.PowerStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerUtil {
	public static boolean hasEnergyStorage(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return false;
		return blockEntity.getCapability(CapabilityEnergy.ENERGY, side).isPresent() || blockEntity instanceof Container;
	}

	public static boolean hasEnergyStorage(BlockGetter world, BlockPos pos, Direction side) {
		return hasEnergyStorage(world.getBlockEntity(pos), side);
	}

	public static IEnergyStorage getEnergyStorage(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return null;
		if (blockEntity.getCapability(CapabilityEnergy.ENERGY, side).isPresent())
			return blockEntity.getCapability(CapabilityEnergy.ENERGY, side).orElse(null);
		return null;
	}

	public static PowerStack insert(BlockEntity destBlockEntity, PowerStack powerStack, Direction side) {
		if (destBlockEntity == null)
			return powerStack;
		IEnergyStorage destHandler = getEnergyStorage(destBlockEntity, side);
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
