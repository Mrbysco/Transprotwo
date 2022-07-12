package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.util.Boost;
import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
	private final int upgrade;
	private final Boost boost;

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
