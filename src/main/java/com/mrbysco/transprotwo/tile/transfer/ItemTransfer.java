package com.mrbysco.transprotwo.tile.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	public void readFromNBT(CompoundTag compound) {
		CompoundTag tag = compound.getCompound("stack");
		stack = ItemStack.of(tag);
		super.readFromNBT(compound);
	}

	public CompoundTag writeToNBT(CompoundTag compound) {
		CompoundTag tag = new CompoundTag();
		stack.save(tag);
		compound.put("stack", tag);
		return super.writeToNBT(compound);
	}

	public static ItemTransfer loadFromNBT(CompoundTag nbt) {
		ItemTransfer transfer = new ItemTransfer();
		transfer.readFromNBT(nbt);
		return transfer;
	}
}
