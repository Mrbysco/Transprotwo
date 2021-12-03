package com.mrbysco.transprotwo.client.screen.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GhostSlot extends SlotItemHandler {

	public GhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		ItemStack holding = playerIn.getInventory().getSelected();

		if (!holding.isEmpty()) {
			holding = holding.copy();
			holding.setCount(1);
		}
		this.set(holding);
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		ItemStack copy = stack.copy();
		copy.setCount(1);
		this.set(copy);
		return false;
	}

	@Override
	public ItemStack remove(int amount) {
		this.set(ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}
}
