package com.mrbysco.transprotwo.tile.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ItemTransfer extends AbstractTransfer {
	public ItemStack stack;

	private ItemTransfer() {
		super(BlockPos.ZERO, BlockPos.ZERO, Direction.DOWN);
	}

	public ItemTransfer(BlockPos dis, BlockPos rec, Direction face, ItemStack stack) {
		super(dis, rec, face);
		this.stack = stack;
	}

	public void readFromNBT(CompoundNBT compound) {
		CompoundNBT tag = compound.getCompound("stack");
		stack = ItemStack.read(tag);
		super.readFromNBT(compound);
	}

	public CompoundNBT writeToNBT(CompoundNBT compound) {
		CompoundNBT tag = new CompoundNBT();
		stack.write(tag);
		compound.put("stack", tag);
		return super.writeToNBT(compound);
	}

	public static ItemTransfer loadFromNBT(CompoundNBT nbt) {
		ItemTransfer transfer = new ItemTransfer();
		transfer.readFromNBT(nbt);
		return transfer;
	}
}
