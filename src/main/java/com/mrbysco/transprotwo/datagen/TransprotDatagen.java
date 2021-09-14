package com.mrbysco.transprotwo.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TransprotDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(new TransprotLoot(generator));
//			generator.addProvider(new TransprotRecipes(generator));
		}
	}

	private static class TransprotLoot extends LootTableProvider {
		public TransprotLoot(DataGenerator gen) {
			super(gen);
		}


		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
			return ImmutableList.of(Pair.of(TransprotBlocks::new, LootParameterSets.BLOCK));
		}

		private static class TransprotBlocks extends BlockLootTables {

			@Override
			protected void addTables() {
				this.registerLootTable(TransprotwoRegistry.DISPATCHER.get(), BlockLootTables::droppingWithName);
				this.registerLootTable(TransprotwoRegistry.FLUID_DISPATCHER.get(), BlockLootTables::droppingWithName);
				this.registerLootTable(TransprotwoRegistry.POWER_DISPATCHER.get(), BlockLootTables::droppingWithName);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) TransprotwoRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
			map.forEach((name, table) -> LootTableManager.validateLootTable(validationtracker, name, table));
		}
	}


	private static class TransprotRecipes extends RecipeProvider {
		public TransprotRecipes(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

		}

		@Override
		protected void saveRecipeAdvancement(DirectoryCache cache, JsonObject advancementJson, Path path) {
			// Nope
		}
	}
}
