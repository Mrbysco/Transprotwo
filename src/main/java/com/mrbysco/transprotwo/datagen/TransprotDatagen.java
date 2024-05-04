package com.mrbysco.transprotwo.datagen;

import com.mrbysco.transprotwo.datagen.client.TransprotLanguageProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotLootProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TransprotDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new TransprotLootProvider(packOutput, lookupProvider));
			generator.addProvider(event.includeServer(), new TransprotRecipeProvider(packOutput, lookupProvider));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new TransprotLanguageProvider(packOutput));
		}
	}
}
