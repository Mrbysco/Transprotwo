package com.mrbysco.transprotwo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InventoryUtil {

	public static boolean hasItemHandler(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side) != null;
	}

	public static IItemHandler getItemHandler(Level level, BlockPos pos, Direction side) {
		return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
	}

	public static ItemStack insert(Level level, BlockPos pos, ItemStack stack, Direction side) {
		IItemHandler inv = getItemHandler(level, pos, side);
		return ItemHandlerHelper.insertItemStacked(inv, stack, false);
	}

	public static int canInsert(IItemHandler inv, ItemStack stack) {
		if (inv == null || stack.isEmpty())
			return 0;
		ItemStack s = ItemHandlerHelper.insertItemStacked(inv, stack, true);
		int rest = s.getCount();
		return stack.getCount() - rest;
	}
}
