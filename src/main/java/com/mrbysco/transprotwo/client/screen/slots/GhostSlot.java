package com.mrbysco.transprotwo.client.screen.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GhostSlot extends SlotItemHandler {

	public GhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(PlayerEntity playerIn) {
		ItemStack holding = playerIn.inventory.getItemStack();

		if (!holding.isEmpty()) {
			holding = holding.copy();
			holding.setCount(1);
		}
		this.putStack(holding);
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		ItemStack copy = stack.copy();
		copy.setCount(1);
		this.putStack(copy);
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		this.putStack(ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}
}
