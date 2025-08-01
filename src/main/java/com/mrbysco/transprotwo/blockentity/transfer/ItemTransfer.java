package com.mrbysco.transprotwo.blockentity.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemTransfer extends AbstractTransfer {
	public ItemStack stack;

	private ItemTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public ItemTransfer(BlockPos dis, BlockPos rec, Direction face, ItemStack stack) {
		super(dis, rec, face);
		this.stack = stack;
	}

	public void readFromNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		stack = ItemStack.parse(lookupProvider, compound.getCompoundOrEmpty("stack")).orElse(ItemStack.EMPTY);
		super.readFromNBT(compound, lookupProvider);
	}

	public CompoundTag writeToNBT(CompoundTag compound, HolderLookup.Provider lookupProvider) {
		compound.put("stack", stack.save(lookupProvider));
		return super.writeToNBT(compound, lookupProvider);
	}

	public static ItemTransfer loadFromNBT(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		ItemTransfer transfer = new ItemTransfer();
		transfer.readFromNBT(nbt, lookupProvider);
		return transfer;
	}
}
