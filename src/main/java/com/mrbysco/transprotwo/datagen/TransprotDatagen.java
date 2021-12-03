package com.mrbysco.transprotwo.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.mrbysco.transprotwo.registry.TransprotwoRegistry.*;

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
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(Pair.of(TransprotBlocks::new, LootContextParamSets.BLOCK));
		}

		private static class TransprotBlocks extends BlockLoot {

			@Override
			protected void addTables() {
				this.add(DISPATCHER.get(), BlockLoot::createNameableBlockEntityTable);
				this.add(FLUID_DISPATCHER.get(), BlockLoot::createNameableBlockEntityTable);
				this.add(POWER_DISPATCHER.get(), BlockLoot::createNameableBlockEntityTable);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return TransprotwoRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}


	private static class TransprotRecipes extends RecipeProvider {
		public TransprotRecipes(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

		}

		@Override
		protected void saveAdvancement(HashCache cache, JsonObject advancementJson, Path path) {
			// Nope
		}
	}
}
