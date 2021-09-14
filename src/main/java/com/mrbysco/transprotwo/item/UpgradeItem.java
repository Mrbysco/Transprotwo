package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.util.Boost;
import net.minecraft.item.Item;

public class UpgradeItem extends Item {
	private int upgrade;
	private Boost boost;

	public UpgradeItem(Properties properties, int upgrade, Boost boost) {
		super(properties);
		this.upgrade = upgrade;
		this.boost = boost;
	}

	public int getUpgrade() {
		return upgrade;
	}

	public Boost getBoost() {
		return boost;
	}
}
