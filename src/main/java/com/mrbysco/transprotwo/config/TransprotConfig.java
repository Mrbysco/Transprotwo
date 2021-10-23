package com.mrbysco.transprotwo.config;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class TransprotConfig {
	public static class Client {
		public final BooleanValue showParticles;
		public final BooleanValue showItems;
		public final BooleanValue showFluids;
		public final BooleanValue showPower;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			showParticles = builder
					.comment("Dictates if the particles are visible")
					.define("showParticles", true);

			showItems = builder
					.comment("Dictates if the Dispatcher renders the items traveling")
					.define("showItems", true);

			showFluids = builder
					.comment("Dictates if the Dispatcher renders the fluid traveling")
					.define("showFluids", true);

			showPower = builder
					.comment("Dictates if the Dispatcher renders the power traveling")
					.define("showPower", true);

			builder.pop();
		}
	}

	public static class Common {
		public final IntValue range;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			range = builder
					.comment("Max distance between dispatcher and inventory [Default: 24]")
					.defineInRange("range", 24, 2, 64);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec clientSpec;
	public static final Client CLIENT;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static final ForgeConfigSpec serverSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		serverSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		Transprotwo.LOGGER.debug("Loaded Transprot's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading configEvent) {
		Transprotwo.LOGGER.debug("Transprot's config just got changed on the file system!");
	}
}
