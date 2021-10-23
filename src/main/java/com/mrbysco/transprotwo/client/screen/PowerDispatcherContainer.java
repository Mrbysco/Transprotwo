package com.mrbysco.transprotwo.client.screen;

import com.mrbysco.transprotwo.client.screen.slots.UpgradeSlot;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;

import java.util.Objects;

public class PowerDispatcherContainer extends Container {
	private PowerDispatcherTile tile;
	private PlayerEntity player;

	public final int[] mode = new int[1];
	public final int[] lines = new int[5];

	public PowerDispatcherContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	private static PowerDispatcherTile getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());

		if (tileAtPos instanceof PowerDispatcherTile) {
			return (PowerDispatcherTile) tileAtPos;
		}

		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	public PowerDispatcherContainer(int id, PlayerInventory playerInventoryIn, PowerDispatcherTile tile) {
		super(TransprotwoContainers.POWER_DISPATCHER.get(), id);
		this.tile = tile;
		this.player = playerInventoryIn.player;

		//Upgrade slot
		this.addSlot(new UpgradeSlot(tile.getUpgrade(), 0, 152, 20));

		//player inventory here
		int xPos = 8;
		int yPos = 111;
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
		this.mode[0] = tile.getMode().getId();
		this.addDataSlot(IntReferenceHolder.shared(this.mode, 0));


		this.lines[0] = tile.getLine1();
		this.addDataSlot(IntReferenceHolder.shared(this.lines, 0));
		this.lines[1] = tile.getLine2();
		this.addDataSlot(IntReferenceHolder.shared(this.lines, 1));
		this.lines[2] = tile.getLine3();
		this.addDataSlot(IntReferenceHolder.shared(this.lines, 2));
		this.lines[3] = tile.getLine4();
		this.addDataSlot(IntReferenceHolder.shared(this.lines, 3));
		this.lines[4] = tile.getLine5();
		this.addDataSlot(IntReferenceHolder.shared(this.lines, 4));
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return this.tile.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
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

	public PowerDispatcherTile getTile() {
		return tile;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public void slotsChanged(IInventory inventoryIn) {
		if(inventoryIn != null) {
			super.slotsChanged(inventoryIn);
		} else {
			this.mode[0] = tile.getMode().getId();
			//TODO BLAH BLAH LINES
		}
	}
}
