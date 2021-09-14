package com.mrbysco.transprotwo.client.screen;

import com.mrbysco.transprotwo.client.screen.slots.GhostSlot;
import com.mrbysco.transprotwo.client.screen.slots.UpgradeSlot;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
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

public class FluidDispatcherContainer extends Container {
	private FluidDispatcherTile tile;
	private PlayerEntity player;

	public final int[] mode = new int[1];
	public final int[] buttonValues = new int[2];

	public FluidDispatcherContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	private static FluidDispatcherTile getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());

		if (tileAtPos instanceof FluidDispatcherTile) {
			return (FluidDispatcherTile) tileAtPos;
		}

		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	public FluidDispatcherContainer(int id, PlayerInventory playerInventoryIn, FluidDispatcherTile tile) {
		super(TransprotwoContainers.FLUID_DISPATCHER.get(), id);
		this.tile = tile;
		this.player = playerInventoryIn.player;

		//Filter slots
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new GhostSlot(tile.getFilter(), j + i * 3, 8 + j * 18, 20 + i * 18));
			}
		}
		//Upgrade slot
		this.addSlot(new UpgradeSlot(tile.getUpgrade(), 0, 152, 20));

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
		this.mode[0] = tile.getMode().getId();
		this.trackInt(IntReferenceHolder.create(this.mode, 0));

		this.buttonValues[0] = tile.isWhite() ? 1 : 0;
		this.trackInt(IntReferenceHolder.create(this.buttonValues, 0));
		this.buttonValues[1] = tile.isMod() ? 1 : 0;
		this.trackInt(IntReferenceHolder.create(this.buttonValues, 1));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return this.tile.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			final int tileSize = 9;

			if (index < tileSize) {
				if (!this.mergeItemStack(itemstack1, tileSize, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, tileSize, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	public FluidDispatcherTile getTile() {
		return tile;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		if(inventoryIn != null) {
			super.onCraftMatrixChanged(inventoryIn);
		} else {
			this.mode[0] = tile.getMode().getId();
			this.buttonValues[0] = tile.isWhite() ? 1 : 0;
			this.buttonValues[1] = tile.isMod() ? 1 : 0;
		}
	}
}
