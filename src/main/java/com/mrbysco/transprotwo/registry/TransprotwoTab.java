package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TransprotwoTab {
	public static final ItemGroup MAIN = new ItemGroup(Transprotwo.MOD_ID) {
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(TransprotwoRegistry.DISPATCHER.get());
		}
	};
}
