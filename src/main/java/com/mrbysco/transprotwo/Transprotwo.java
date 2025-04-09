package com.mrbysco.transprotwo;

import com.mojang.logging.LogUtils;
import com.mrbysco.transprotwo.client.ClientHandler;
import com.mrbysco.transprotwo.client.particles.TransprotwoParticles;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.registry.TransprotwoComponents;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(Transprotwo.MOD_ID)
public class Transprotwo {
	public static final String MOD_ID = "transprotwo";
	public static final Logger LOGGER = LogUtils.getLogger();

	public Transprotwo(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, TransprotConfig.serverSpec);
		eventBus.register(TransprotConfig.class);

		eventBus.addListener(PacketHandler::setupPackets);

		TransprotwoComponents.DATA_COMPONENT_TYPES.register(eventBus);
		TransprotwoRegistry.ITEMS.register(eventBus);
		TransprotwoRegistry.BLOCKS.register(eventBus);
		TransprotwoRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		TransprotwoRegistry.CREATIVE_MODE_TABS.register(eventBus);
		TransprotwoContainers.MENU_TYPES.register(eventBus);

		TransprotwoParticles.PARTICLE_TYPES.register(eventBus);

		if (dist.isClient()) {
			container.registerConfig(ModConfig.Type.CLIENT, TransprotConfig.clientSpec);
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::registerMenus);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerRenderTypes);
		}
	}

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
