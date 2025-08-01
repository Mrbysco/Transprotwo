package com.mrbysco.transprotwo.datagen;

import com.mrbysco.transprotwo.datagen.client.TransprotLanguageProvider;
import com.mrbysco.transprotwo.datagen.client.TransprotModelProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotLootProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class TransprotDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new TransprotLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new TransprotRecipeProvider.Runner(packOutput, lookupProvider));

		generator.addProvider(true, new TransprotLanguageProvider(packOutput));
		generator.addProvider(true, new TransprotModelProvider(packOutput));
	}
}
