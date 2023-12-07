package com.mrbysco.transprotwo.datagen;

import com.mrbysco.transprotwo.datagen.client.TransprotLanguageProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotLootProvider;
import com.mrbysco.transprotwo.datagen.server.TransprotRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TransprotDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new TransprotLootProvider(packOutput));
			generator.addProvider(event.includeServer(), new TransprotRecipeProvider(packOutput, event.getLookupProvider()));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new TransprotLanguageProvider(packOutput));
		}
	}
}
