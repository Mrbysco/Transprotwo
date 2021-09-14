package com.mrbysco.transprotwo.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InventoryUtil {
	public static boolean hasItemHandler(TileEntity tile, Direction side) {
		if (tile == null)
			return false;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent() || tile instanceof IInventory;
	}

	public static boolean hasItemHandler(IBlockReader world, BlockPos pos, Direction side) {
		return hasItemHandler(world.getTileEntity(pos), side);
	}

	public static IItemHandler getItemHandler(TileEntity tile, Direction side) {
		if (tile == null)
			return null;
		if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent())
			return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
		if (tile instanceof ISidedInventory)
			return new SidedInvWrapper((ISidedInventory) tile, side);
		if (tile instanceof IInventory)
			return new InvWrapper((IInventory) tile);
		return null;
	}

	public static ItemStack insert(TileEntity tile, ItemStack stack, Direction side) {
		if (tile == null)
			return stack;
		IItemHandler inv = getItemHandler(tile, side);
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
