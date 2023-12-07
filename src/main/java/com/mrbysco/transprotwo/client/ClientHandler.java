package com.mrbysco.transprotwo.client;

import com.mrbysco.transprotwo.client.renderer.ber.FluidDispatcherBER;
import com.mrbysco.transprotwo.client.renderer.ber.ItemDispatcherBER;
import com.mrbysco.transprotwo.client.renderer.ber.PowerDispatcherBER;
import com.mrbysco.transprotwo.client.screen.DispatcherScreen;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherScreen;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherScreen;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		MenuScreens.register(TransprotwoContainers.DISPATCHER.get(), DispatcherScreen::new);
		MenuScreens.register(TransprotwoContainers.FLUID_DISPATCHER.get(), FluidDispatcherScreen::new);
		MenuScreens.register(TransprotwoContainers.POWER_DISPATCHER.get(), PowerDispatcherScreen::new);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(TransprotwoRegistry.DISPATCHER_BLOCK_ENTITY.get(), ItemDispatcherBER::new);
		event.registerBlockEntityRenderer(TransprotwoRegistry.FLUID_DISPATCHER_BLOCK_ENTITY.get(), FluidDispatcherBER::new);
		event.registerBlockEntityRenderer(TransprotwoRegistry.POWER_DISPATCHER_BLOCK_ENTITY.get(), PowerDispatcherBER::new);
	}
}
