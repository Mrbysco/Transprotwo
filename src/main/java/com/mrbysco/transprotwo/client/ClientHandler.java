package com.mrbysco.transprotwo.client;

import com.mrbysco.transprotwo.client.renderer.TransprotwoRenderTypes;
import com.mrbysco.transprotwo.client.renderer.ber.FluidDispatcherBER;
import com.mrbysco.transprotwo.client.renderer.ber.ItemDispatcherBER;
import com.mrbysco.transprotwo.client.renderer.ber.PowerDispatcherBER;
import com.mrbysco.transprotwo.client.screen.DispatcherScreen;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherScreen;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherScreen;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;

public class ClientHandler {
	public static void registerMenus(final RegisterMenuScreensEvent event) {
		event.register(TransprotwoContainers.DISPATCHER.get(), DispatcherScreen::new);
		event.register(TransprotwoContainers.FLUID_DISPATCHER.get(), FluidDispatcherScreen::new);
		event.register(TransprotwoContainers.POWER_DISPATCHER.get(), PowerDispatcherScreen::new);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TransprotwoRegistry.DISPATCHER_BLOCK_ENTITY.get(), ItemDispatcherBER::new);
		event.registerBlockEntityRenderer(TransprotwoRegistry.FLUID_DISPATCHER_BLOCK_ENTITY.get(), FluidDispatcherBER::new);
		event.registerBlockEntityRenderer(TransprotwoRegistry.POWER_DISPATCHER_BLOCK_ENTITY.get(), PowerDispatcherBER::new);
	}

	public static void registerRenderTypes(final RegisterRenderBuffersEvent event) {
		event.registerRenderBuffer(TransprotwoRenderTypes.LINE);
		event.registerRenderBuffer(TransprotwoRenderTypes.LINE_4);
		event.registerRenderBuffer(TransprotwoRenderTypes.POWER);
		event.registerRenderBuffer(TransprotwoRenderTypes.LIQUID);
	}
}
