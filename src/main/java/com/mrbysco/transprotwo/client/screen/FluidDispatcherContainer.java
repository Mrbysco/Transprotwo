package com.mrbysco.transprotwo.client.screen;

import com.mrbysco.transprotwo.client.screen.slots.GhostSlot;
import com.mrbysco.transprotwo.client.screen.slots.UpgradeSlot;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class FluidDispatcherContainer extends AbstractContainerMenu {
	private FluidDispatcherBE blockEntity;
	private Player player;

	public final int[] mode = new int[1];
	public final int[] buttonValues = new int[2];

	public FluidDispatcherContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
		this(windowId, playerInventory, getBlockEntity(playerInventory, data));
	}

	private static FluidDispatcherBE getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final BlockEntity blockEntityAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());

		if (blockEntityAtPos instanceof FluidDispatcherBE) {
			return (FluidDispatcherBE) blockEntityAtPos;
		}

		throw new IllegalStateException("Block entity is not correct! " + blockEntityAtPos);
	}

	public FluidDispatcherContainer(int id, Inventory playerInventoryIn, FluidDispatcherBE fluidDispatcher) {
		super(TransprotwoContainers.FLUID_DISPATCHER.get(), id);
		this.blockEntity = fluidDispatcher;
		this.player = playerInventoryIn.player;

		//Filter slots
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlot(new GhostSlot(fluidDispatcher.getFilter(), j + i * 3, 8 + j * 18, 20 + i * 18));
			}
		}
		//Upgrade slot
		this.addSlot(new UpgradeSlot(fluidDispatcher.getUpgrade(), 0, 152, 20));

		//player inventory here
		int xPos = 8;
		int yPos = 90;
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlot(new Slot(playerInventoryIn, x + y * 9 + 9, xPos + x * 18, yPos + y * 18));
			}
		}

		for (int x = 0; x < 9; ++x) {
			this.addSlot(new Slot(playerInventoryIn, x, xPos + x * 18, yPos + 58));
		}

		trackValues();
	}

	public void trackValues() {
		this.mode[0] = blockEntity.getMode().getId();
		this.addDataSlot(DataSlot.shared(this.mode, 0));

		this.buttonValues[0] = blockEntity.isWhite() ? 1 : 0;
		this.addDataSlot(DataSlot.shared(this.buttonValues, 0));
		this.buttonValues[1] = blockEntity.isMod() ? 1 : 0;
		this.addDataSlot(DataSlot.shared(this.buttonValues, 1));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return this.blockEntity.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			final int tileSize = 9;

			if (index < tileSize) {
				if (!this.moveItemStackTo(itemstack1, tileSize, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, tileSize, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	public FluidDispatcherBE getBlockEntity() {
		return blockEntity;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		if (inventoryIn != null) {
			super.slotsChanged(inventoryIn);
		} else {
			this.mode[0] = blockEntity.getMode().getId();
			this.buttonValues[0] = blockEntity.isWhite() ? 1 : 0;
			this.buttonValues[1] = blockEntity.isMod() ? 1 : 0;
		}
	}
}
