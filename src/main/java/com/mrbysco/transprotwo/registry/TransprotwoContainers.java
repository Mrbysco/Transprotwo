package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.DispatcherContainer;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherContainer;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TransprotwoContainers {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Transprotwo.MOD_ID);

	public static final net.minecraftforge.registries.RegistryObject<MenuType<DispatcherContainer>> DISPATCHER = MENU_TYPES.register("dispatcher", () ->
			IForgeMenuType.create((windowId, inv, data) -> new DispatcherContainer(windowId, inv, data)));

	public static final net.minecraftforge.registries.RegistryObject<MenuType<FluidDispatcherContainer>> FLUID_DISPATCHER = MENU_TYPES.register("fluid_dispatcher", () ->
			IForgeMenuType.create((windowId, inv, data) -> new FluidDispatcherContainer(windowId, inv, data)));

	public static final RegistryObject<MenuType<PowerDispatcherContainer>> POWER_DISPATCHER = MENU_TYPES.register("power_dispatcher", () ->
			IForgeMenuType.create((windowId, inv, data) -> new PowerDispatcherContainer(windowId, inv, data)));
}
