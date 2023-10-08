package com.mrbysco.transprotwo.datagen.server;

import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class TransprotRecipeProvider extends RecipeProvider {

	public TransprotRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TransprotwoRegistry.DISPATCHER.get(), 2)
				.pattern("EIE")
				.pattern("ICI")
				.pattern("I I")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('C', Tags.Items.CHESTS_WOODEN)
				.unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.unlockedBy("has_wooden_chest", has(Tags.Items.CHESTS_WOODEN))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TransprotwoRegistry.FLUID_DISPATCHER.get(), 2)
				.pattern("EIE")
				.pattern("IBI")
				.pattern("I I")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('B', Items.BUCKET)
				.unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.unlockedBy("has_bucket", has(Items.BUCKET))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TransprotwoRegistry.POWER_DISPATCHER.get(), 2)
				.pattern("EIE")
				.pattern("IRI")
				.pattern("I I")
				.define('E', Tags.Items.ENDER_PEARLS)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_ender_pearl", has(Tags.Items.ENDER_PEARLS))
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TransprotwoRegistry.LINKER.get())
				.pattern("I  ")
				.pattern(" P ")
				.pattern("  I")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('P', Items.PAPER)
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.unlockedBy("has_paper", has(Items.PAPER))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TransprotwoRegistry.UPGRADE_MK_I.get())
				.requires(Tags.Items.DUSTS_REDSTONE)
				.requires(Tags.Items.INGOTS_GOLD)
				.requires(Items.PAPER)
				.requires(Tags.Items.INGOTS_IRON)
				.unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
				.unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
				.unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
				.unlockedBy("has_paper", has(Items.PAPER))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TransprotwoRegistry.UPGRADE_MK_II.get())
				.pattern("UGU")
				.define('U', TransprotwoRegistry.UPGRADE_MK_I.get())
				.define('G', Tags.Items.INGOTS_GOLD)
				.unlockedBy("has_upgrade", has(TransprotwoRegistry.UPGRADE_MK_I.get()))
				.unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TransprotwoRegistry.UPGRADE_MK_III.get())
				.pattern("UDU")
				.define('U', TransprotwoRegistry.UPGRADE_MK_II.get())
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_upgrade", has(TransprotwoRegistry.UPGRADE_MK_II.get()))
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TransprotwoRegistry.UPGRADE_MK_IV.get())
				.pattern("UEU")
				.define('U', TransprotwoRegistry.UPGRADE_MK_III.get())
				.define('E', Tags.Items.GEMS_EMERALD)
				.unlockedBy("has_upgrade", has(TransprotwoRegistry.UPGRADE_MK_III.get()))
				.unlockedBy("has_emerald", has(Tags.Items.GEMS_EMERALD))
				.save(consumer);
	}
}
