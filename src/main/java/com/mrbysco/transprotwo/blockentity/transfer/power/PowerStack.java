package com.mrbysco.transprotwo.blockentity.transfer.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class PowerStack {

	public static final Codec<PowerStack> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Codec.INT.optionalFieldOf("amount", new Random().nextInt()).forGetter(transfer -> transfer.amount)
					)
					.apply(instance, PowerStack::new)
	);

	public static final PowerStack EMPTY = new PowerStack(0);

	private boolean isEmpty;
	private final int amount;

	public PowerStack(int amount) {
		this.amount = amount;

		updateEmpty();
	}

	public static PowerStack read(CompoundTag nbt) {
		if (nbt == null) {
			return EMPTY;
		}

		PowerStack stack = new PowerStack(nbt.getIntOr("Amount", 0));

		return stack;
	}

	public CompoundTag write(CompoundTag nbt) {
		nbt.putInt("Amount", amount);
		return nbt;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	protected void updateEmpty() {
		isEmpty = amount <= 0;
	}

	public int getAmount() {
		return isEmpty ? 0 : amount;
	}

	public String toString() {
		return "PowerStack[ " + this.amount + " ]";
	}

}
