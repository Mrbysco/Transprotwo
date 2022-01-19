package com.mrbysco.transprotwo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InventoryUtil {
	public static boolean hasItemHandler(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return false;
		return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent() || blockEntity instanceof Container;
	}

	public static boolean hasItemHandler(BlockGetter world, BlockPos pos, Direction side) {
		return hasItemHandler(world.getBlockEntity(pos), side);
	}

	public static IItemHandler getItemHandler(BlockEntity blockEntity, Direction side) {
		if (blockEntity == null)
			return null;
		if (blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent())
			return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
		if (blockEntity instanceof WorldlyContainer)
			return new SidedInvWrapper((WorldlyContainer) blockEntity, side);
		if (blockEntity instanceof Container)
			return new InvWrapper((Container) blockEntity);
		return null;
	}

	public static ItemStack insert(BlockEntity blockEntity, ItemStack stack, Direction side) {
		if (blockEntity == null)
			return stack;
		IItemHandler inv = getItemHandler(blockEntity, side);
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
