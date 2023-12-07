package com.mrbysco.transprotwo.config;

import com.mrbysco.transprotwo.Transprotwo;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class TransprotConfig {
	public static class Client {
		public final BooleanValue showParticles;
		public final BooleanValue showItems;
		public final BooleanValue showFluids;
		public final BooleanValue showPower;

		Client(ModConfigSpec.Builder builder) {
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

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			range = builder
					.comment("Max distance between dispatcher and inventory [Default: 24]")
					.defineInRange("range", 24, 2, 64);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static final ModConfigSpec serverSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		serverSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		Transprotwo.LOGGER.debug("Loaded Transprot's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		Transprotwo.LOGGER.warn("Transprot's config just got changed on the file system!");
	}
}
