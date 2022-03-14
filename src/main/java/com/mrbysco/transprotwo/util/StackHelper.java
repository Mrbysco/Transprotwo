package com.mrbysco.transprotwo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StackHelper {
	public static boolean matchAnyTag(ItemStack stack1, ItemStack stack2) {
		Set<ResourceLocation> tagLocations = stack1.getTags().map((tagKey) -> tagKey.location()).collect(Collectors.toSet());
		Set<ResourceLocation> otherLocations = stack2.getTags().map((tagKey) -> tagKey.location()).collect(Collectors.toSet());
		for (ResourceLocation tagLoc : tagLocations) {
			if (otherLocations.contains(tagLoc)) {
				return true;
			}
		}
		return false;
	}
}
