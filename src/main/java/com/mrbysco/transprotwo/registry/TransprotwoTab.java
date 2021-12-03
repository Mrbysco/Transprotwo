package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TransprotwoTab {
	public static final CreativeModeTab MAIN = new CreativeModeTab(Transprotwo.MOD_ID) {
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(TransprotwoRegistry.DISPATCHER.get());
		}
	};
}
