package com.mrbysco.transprotwo.tile.transfer.power;

import net.minecraft.nbt.CompoundNBT;

public class PowerStack {
	public static final PowerStack EMPTY = new PowerStack(0);

	private boolean isEmpty;
	private final int amount;

	public PowerStack(int amount) {
		this.amount = amount;

		updateEmpty();
	}

	public static PowerStack read(CompoundNBT nbt) {
		if (nbt == null) {
			return EMPTY;
		}

		PowerStack stack = new PowerStack(nbt.getInt("Amount"));

		return stack;
	}

	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putInt("Amount", amount);
		return nbt;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	protected void updateEmpty() {
		isEmpty = amount <= 0;
	}

	public int getAmount()
	{
		return isEmpty ? 0 : amount ;
	}

	public String toString() {
		return "PowerStack[ " + this.amount + " ]";
	}

}
