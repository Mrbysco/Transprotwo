package com.mrbysco.transprotwo.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class StackHelper {
	public static boolean matchAnyTag(ItemStack stack1, ItemStack stack2) {
		Set<ResourceLocation> tagLocations = stack1.getItem().getTags();
		ITagCollectionSupplier tagCollection = TagCollectionManager.getInstance();
		for(ResourceLocation tagLoc : tagLocations) {
			Tag<Item> tagContents = (Tag<Item>) tagCollection.getItems().getTag(tagLoc);
			if(tagContents != null) {
				if(stack2.getItem().is(tagContents)) {
					return true;
				}
			}
		}
		return false;
	}
}
