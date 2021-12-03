package com.mrbysco.transprotwo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class StackHelper {
	public static boolean matchAnyTag(ItemStack stack1, ItemStack stack2) {
		Set<ResourceLocation> tagLocations = stack1.getItem().getTags();
		Set<ResourceLocation> otherLocations = stack2.getItem().getTags();
		for(ResourceLocation tagLoc : tagLocations) {
			if(otherLocations.contains(tagLoc)) {
				return true;
			}
		}
		return false;
	}
}
