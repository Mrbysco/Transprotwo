package com.mrbysco.transprotwo.client;

import com.mrbysco.transprotwo.client.renderer.DispatcherTESR;
import com.mrbysco.transprotwo.client.screen.DispatcherScreen;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherScreen;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherScreen;
import com.mrbysco.transprotwo.registry.TransprotwoContainers;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(TransprotwoRegistry.DISPATCHER_TILE.get(), DispatcherTESR::new);
		ClientRegistry.bindTileEntityRenderer(TransprotwoRegistry.FLUID_DISPATCHER_TILE.get(), DispatcherTESR::new);
		ClientRegistry.bindTileEntityRenderer(TransprotwoRegistry.POWER_DISPATCHER_TILE.get(), DispatcherTESR::new);

		ScreenManager.register(TransprotwoContainers.DISPATCHER.get(), DispatcherScreen::new);
		ScreenManager.register(TransprotwoContainers.FLUID_DISPATCHER.get(), FluidDispatcherScreen::new);
		ScreenManager.register(TransprotwoContainers.POWER_DISPATCHER.get(), PowerDispatcherScreen::new);
	}
}
