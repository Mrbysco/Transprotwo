package com.mrbysco.transprotwo;

import com.mojang.logging.LogUtils;
import com.mrbysco.transprotwo.client.ClientHandler;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Transprotwo.MOD_ID)
public class Transprotwo {
	public static final String MOD_ID = "transprotwo";
	public static final Logger LOGGER = LogUtils.getLogger();

	public Transprotwo(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TransprotConfig.clientSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TransprotConfig.serverSpec);
		eventBus.register(TransprotConfig.class);

		eventBus.addListener(PacketHandler::setupPackets);

		TransprotwoRegistry.ITEMS.register(eventBus);
		TransprotwoRegistry.BLOCKS.register(eventBus);
		TransprotwoRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		TransprotwoRegistry.CREATIVE_MODE_TABS.register(eventBus);
		TransprotwoContainers.MENU_TYPES.register(eventBus);

		TransprotwoParticles.PARTICLE_TYPES.register(eventBus);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerEntityRenders);
		}
	}
	
}
