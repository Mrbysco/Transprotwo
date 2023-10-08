package com.mrbysco.transprotwo.datagen;

import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mrbysco.transprotwo.registry.TransprotwoRegistry.DISPATCHER;
import static com.mrbysco.transprotwo.registry.TransprotwoRegistry.FLUID_DISPATCHER;
import static com.mrbysco.transprotwo.registry.TransprotwoRegistry.POWER_DISPATCHER;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TransprotDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new TransprotLoot(packOutput));
		}
	}

	private static class TransprotLoot extends LootTableProvider {
		public TransprotLoot(PackOutput packOutput) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(TransprotBlocks::new, LootContextParamSets.BLOCK)
			));
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
			map.forEach((name, table) -> table.validate(validationtracker));
		}

		private static class TransprotBlocks extends BlockLootSubProvider {

			protected TransprotBlocks() {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			protected void generate() {
				this.add(DISPATCHER.get(), this::createNameableBlockEntityTable);
				this.add(FLUID_DISPATCHER.get(), this::createNameableBlockEntityTable);
				this.add(POWER_DISPATCHER.get(), this::createNameableBlockEntityTable);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return TransprotwoRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}
	}
}
